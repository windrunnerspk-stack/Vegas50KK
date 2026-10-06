package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppCurrency
import com.example.data.local.AppDatabase
import com.example.data.local.BetEntity
import com.example.data.local.RiskSettingsEntity
import com.example.data.local.UserAccountEntity
import com.example.data.repository.VegasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

private data class FilterState(
    val period: PeriodFilter,
    val category: CategoryFilter,
    val status: StatusFilter,
    val query: String
)

class VegasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VegasRepository

    init {
        val db = AppDatabase.getDatabase(application)
        val firebaseAuth = com.example.data.auth.FirebaseAuthService(application)
        repository = VegasRepository(
            context = application,
            betDao = db.betDao(),
            riskSettingsDao = db.riskSettingsDao(),
            userDao = db.userDao(),
            firebaseAuthService = firebaseAuth
        )
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.THIS_WEEK)
    private val _selectedCategory = MutableStateFlow(CategoryFilter.ALL)
    private val _selectedStatus = MutableStateFlow(StatusFilter.ALL)
    private val _searchQuery = MutableStateFlow("")

    private val _editingBet = MutableStateFlow<BetEntity?>(null)
    private val _isAddEditSheetOpen = MutableStateFlow(false)
    private val _isRiskSettingsOpen = MutableStateFlow(false)
    private val _isCurrencyDialogOpen = MutableStateFlow(false)
    private val _isAuthDialogOpen = MutableStateFlow(false)
    private val _isEmergencyResetOpen = MutableStateFlow(false)
    private val _isGuestSession = MutableStateFlow(false)
    private val _messageSnackbar = MutableStateFlow<String?>(null)

    private val filterStateFlow = combine(
        _selectedPeriod,
        _selectedCategory,
        _selectedStatus,
        _searchQuery
    ) { period, category, status, query ->
        FilterState(period, category, status, query)
    }

    val uiState: StateFlow<VegasUiState> = combine(
        repository.allBets,
        repository.riskSettings,
        repository.activeUser,
        filterStateFlow
    ) { allBets: List<BetEntity>, settings: RiskSettingsEntity?, activeUser: UserAccountEntity?, filter: FilterState ->
        val currentSettings = settings ?: RiskSettingsEntity()
        val currentCurrency = AppCurrency.fromCode(currentSettings.currencyCode)
        val now = System.currentTimeMillis()
        val oneDayMs = 86_400_000L
        val weekCutoff = now - (7 * oneDayMs)
        val monthCutoff = now - (30 * oneDayMs)

        // 1. Calculate Global Bankroll across ALL resolved bets
        val allResolvedBets = allBets.filter { it.status == "WON" || it.status == "LOST" || it.status == "REFUNDED" }
        val cumulativeProfitAllTime = allResolvedBets.sumOf { it.netProfit }
        val currentBankroll = currentSettings.startingBankroll + cumulativeProfitAllTime

        // 2. Filter bets by period for period KPIs
        val periodBets = allBets.filter { bet ->
            when (filter.period) {
                PeriodFilter.THIS_WEEK -> bet.timestamp >= weekCutoff
                PeriodFilter.THIS_MONTH -> bet.timestamp >= monthCutoff
                PeriodFilter.ALL_TIME -> true
            }
        }

        val periodStake = periodBets.sumOf { it.stake }
        val periodGrossProfit = periodBets.filter { it.status == "WON" }.sumOf { it.payout }
        val periodLosses = periodBets.filter { it.status == "LOST" }.sumOf { it.stake }
        val periodNetProfit = periodBets.sumOf { it.netProfit }
        val periodRoi = if (periodStake > 0.0) (periodNetProfit / periodStake) * 100.0 else 0.0

        val wonCount = periodBets.count { it.status == "WON" }
        val lostCount = periodBets.count { it.status == "LOST" }
        val pendingCount = periodBets.count { it.status == "PENDING" }
        val refundedCount = periodBets.count { it.status == "REFUNDED" }
        val resolvedCount = wonCount + lostCount
        val winRate = if (resolvedCount > 0) (wonCount.toDouble() / resolvedCount) * 100.0 else 0.0

        // 3. Weekly Risk Management metrics (strictly last 7 days)
        val weeklyBets = allBets.filter { it.timestamp >= weekCutoff }
        val weeklyStakeUsed = weeklyBets.sumOf { it.stake }
        val weeklyLossUsed = weeklyBets.filter { it.status == "LOST" }.sumOf { it.stake }
        val lossRatio = if (currentSettings.weeklyLossLimit > 0.0) (weeklyLossUsed / currentSettings.weeklyLossLimit) * 100.0 else 0.0
        val stakeRatio = if (currentSettings.weeklyStakeLimit > 0.0) (weeklyStakeUsed / currentSettings.weeklyStakeLimit) * 100.0 else 0.0
        val riskScorePercent = max(lossRatio.toFloat(), stakeRatio.toFloat())

        val riskStatus = when {
            riskScorePercent >= 90f -> RiskStatus.CRITICAL
            riskScorePercent >= 70f -> RiskStatus.WARNING
            else -> RiskStatus.SAFE
        }

        // 4. Streak analysis & stats
        val sortedResolvedChronological = allBets
            .filter { it.status == "WON" || it.status == "LOST" }
            .sortedBy { it.timestamp }

        var currentStreak = 0
        if (sortedResolvedChronological.isNotEmpty()) {
            val lastIsWin = sortedResolvedChronological.last().status == "WON"
            var streakCount = 0
            for (i in sortedResolvedChronological.indices.reversed()) {
                val b = sortedResolvedChronological[i]
                if ((lastIsWin && b.status == "WON") || (!lastIsWin && b.status == "LOST")) {
                    streakCount++
                } else {
                    break
                }
            }
            currentStreak = if (lastIsWin) streakCount else -streakCount
        }

        val biggestWin = allBets.filter { it.status == "WON" }.maxOfOrNull { it.payout - it.stake } ?: 0.0
        val averageOdds = if (allBets.isNotEmpty()) allBets.map { it.odds }.average() else 0.0

        val kpis = KpiSummary(
            currentBankroll = currentBankroll,
            startingBankroll = currentSettings.startingBankroll,
            periodStake = periodStake,
            periodGrossProfit = periodGrossProfit,
            periodLosses = periodLosses,
            periodNetProfit = periodNetProfit,
            periodRoi = periodRoi,
            winRate = winRate,
            totalBetsCount = periodBets.size,
            wonCount = wonCount,
            lostCount = lostCount,
            pendingCount = pendingCount,
            refundedCount = refundedCount,
            weeklyLossUsed = weeklyLossUsed,
            weeklyStakeUsed = weeklyStakeUsed,
            weeklyLossLimit = currentSettings.weeklyLossLimit,
            weeklyStakeLimit = currentSettings.weeklyStakeLimit,
            riskScorePercent = riskScorePercent,
            riskStatus = riskStatus,
            currentStreak = currentStreak,
            biggestWin = biggestWin,
            averageOdds = averageOdds
        )

        // 5. Chart data points
        val chartPoints = buildChartPoints(currentSettings.startingBankroll, allBets)

        // 6. Category comparison stats
        val sportsBets = periodBets.filter { it.category == "SPORTS" }
        val casinoBets = periodBets.filter { it.category == "CASINO" }

        val sportsStake = sportsBets.sumOf { it.stake }
        val sportsReturn = sportsBets.filter { it.status == "WON" }.sumOf { it.payout }
        val sportsNetProfit = sportsBets.sumOf { it.netProfit }
        val sportsResolved = sportsBets.count { it.status == "WON" || it.status == "LOST" }
        val sportsWon = sportsBets.count { it.status == "WON" }
        val sportsWinRate = if (sportsResolved > 0) (sportsWon.toDouble() / sportsResolved) * 100.0 else 0.0

        val casinoStake = casinoBets.sumOf { it.stake }
        val casinoReturn = casinoBets.filter { it.status == "WON" }.sumOf { it.payout }
        val casinoNetProfit = casinoBets.sumOf { it.netProfit }
        val casinoResolved = casinoBets.count { it.status == "WON" || it.status == "LOST" }
        val casinoWon = casinoBets.count { it.status == "WON" }
        val casinoWinRate = if (casinoResolved > 0) (casinoWon.toDouble() / casinoResolved) * 100.0 else 0.0

        val categoryStats = CategoryStats(
            sportsStake = sportsStake,
            sportsReturn = sportsReturn,
            sportsNetProfit = sportsNetProfit,
            sportsWinRate = sportsWinRate,
            sportsCount = sportsBets.size,
            casinoStake = casinoStake,
            casinoReturn = casinoReturn,
            casinoNetProfit = casinoNetProfit,
            casinoWinRate = casinoWinRate,
            casinoCount = casinoBets.size
        )

        // 7. Filter bets for movement list/table
        val filteredList = periodBets.filter { bet ->
            val matchesCategory = when (filter.category) {
                CategoryFilter.ALL -> true
                CategoryFilter.SPORTS -> bet.category == "SPORTS"
                CategoryFilter.CASINO -> bet.category == "CASINO"
            }
            val matchesStatus = when (filter.status) {
                StatusFilter.ALL -> true
                StatusFilter.WON -> bet.status == "WON"
                StatusFilter.LOST -> bet.status == "LOST"
                StatusFilter.PENDING -> bet.status == "PENDING"
                StatusFilter.REFUNDED -> bet.status == "REFUNDED"
            }
            val matchesSearch = if (filter.query.isBlank()) {
                true
            } else {
                val q = filter.query.trim().lowercase(Locale.ROOT)
                bet.eventName.lowercase(Locale.ROOT).contains(q) ||
                        bet.subcategory.lowercase(Locale.ROOT).contains(q) ||
                        bet.market.lowercase(Locale.ROOT).contains(q) ||
                        bet.notes.lowercase(Locale.ROOT).contains(q)
            }
            matchesCategory && matchesStatus && matchesSearch
        }

        VegasUiState(
            isLoading = false,
            allBets = allBets,
            filteredBets = filteredList,
            kpis = kpis,
            chartPoints = chartPoints,
            categoryStats = categoryStats,
            selectedPeriod = filter.period,
            selectedCategory = filter.category,
            selectedStatus = filter.status,
            searchQuery = filter.query,
            currency = currentCurrency,
            activeUser = activeUser
        )
    }.combine(_editingBet) { state, editBet ->
        state.copy(editingBet = editBet)
    }.combine(_isAddEditSheetOpen) { state, isOpen ->
        state.copy(isAddEditSheetOpen = isOpen)
    }.combine(_isRiskSettingsOpen) { state, isRiskOpen ->
        state.copy(isRiskSettingsOpen = isRiskOpen)
    }.combine(_isCurrencyDialogOpen) { state, isCurrOpen ->
        state.copy(isCurrencyDialogOpen = isCurrOpen)
    }.combine(_isAuthDialogOpen) { state, isAuthOpen ->
        state.copy(isAuthDialogOpen = isAuthOpen)
    }.combine(_isEmergencyResetOpen) { state, isResetOpen ->
        state.copy(isEmergencyResetOpen = isResetOpen)
    }.combine(_isGuestSession) { state, isGuest ->
        state.copy(isGuestSession = isGuest)
    }.combine(_messageSnackbar) { state, snackbar ->
        state.copy(messageSnackbar = snackbar)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VegasUiState(isLoading = true)
    )

    private fun buildChartPoints(initialBankroll: Double, bets: List<BetEntity>): List<ChartDataPoint> {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val chronological = bets
            .filter { it.status == "WON" || it.status == "LOST" || it.status == "REFUNDED" }
            .sortedBy { it.timestamp }

        if (chronological.isEmpty()) {
            return listOf(
                ChartDataPoint(label = "Inicio", bankrollValue = initialBankroll, netChange = 0.0, timestamp = System.currentTimeMillis())
            )
        }

        val points = mutableListOf<ChartDataPoint>()
        val firstTime = chronological.first().timestamp - 86_400_000L
        points.add(ChartDataPoint(label = "Base", bankrollValue = initialBankroll, netChange = 0.0, timestamp = firstTime))

        var runningBankroll = initialBankroll
        chronological.forEach { bet ->
            runningBankroll += bet.netProfit
            points.add(
                ChartDataPoint(
                    label = dateFormat.format(Date(bet.timestamp)),
                    bankrollValue = runningBankroll,
                    netChange = bet.netProfit,
                    timestamp = bet.timestamp
                )
            )
        }
        return points
    }

    fun setPeriod(period: PeriodFilter) {
        _selectedPeriod.value = period
    }

    fun setCategory(category: CategoryFilter) {
        _selectedCategory.value = category
    }

    fun setStatus(status: StatusFilter) {
        _selectedStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddBetSheet(betToEdit: BetEntity? = null) {
        _editingBet.value = betToEdit
        _isAddEditSheetOpen.value = true
    }

    fun closeAddEditSheet() {
        _editingBet.value = null
        _isAddEditSheetOpen.value = false
    }

    fun openRiskSettings() {
        _isRiskSettingsOpen.value = true
    }

    fun closeRiskSettings() {
        _isRiskSettingsOpen.value = false
    }

    fun openCurrencyDialog() {
        _isCurrencyDialogOpen.value = true
    }

    fun closeCurrencyDialog() {
        _isCurrencyDialogOpen.value = false
    }

    fun setCurrency(currency: AppCurrency) {
        viewModelScope.launch {
            repository.setCurrency(currency.code)
            _isCurrencyDialogOpen.value = false
            _messageSnackbar.value = "Moneda configurada a ${currency.displayName} (${currency.code})"
        }
    }

    fun openAuthDialog() {
        _isAuthDialogOpen.value = true
    }

    fun closeAuthDialog() {
        _isAuthDialogOpen.value = false
    }

    fun signInWithGoogle(activityContext: android.content.Context, onFinished: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(activityContext)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _messageSnackbar.value = "¡Bienvenido a Vegas 50k, ${user?.displayName ?: "Usuario VIP"}!"
                _isAuthDialogOpen.value = false
                onFinished(true, null)
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Error al autenticar con Google"
                onFinished(false, err)
            }
        }
    }

    fun continueAsGuest() {
        _isGuestSession.value = true
        _messageSnackbar.value = "Modo exploración activo"
    }

    fun logoutUser() {
        viewModelScope.launch {
            _isGuestSession.value = false
            repository.logoutUser()
            _messageSnackbar.value = "Has cerrado sesión"
        }
    }

    fun saveBet(
        id: Long = 0,
        category: String,
        subcategory: String,
        eventName: String,
        market: String,
        odds: Double,
        stake: Double,
        status: String,
        payout: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val bet = BetEntity(
                id = id,
                category = category,
                subcategory = subcategory,
                eventName = eventName.trim(),
                market = market.trim(),
                odds = odds,
                stake = stake,
                status = status,
                payout = payout,
                notes = notes.trim(),
                timestamp = if (id != 0L) {
                    _editingBet.value?.timestamp ?: System.currentTimeMillis()
                } else {
                    System.currentTimeMillis()
                }
            )
            if (id == 0L) {
                repository.insertBet(bet)
                _messageSnackbar.value = "Apuesta registrada correctamente"
            } else {
                repository.updateBet(bet)
                _messageSnackbar.value = "Apuesta actualizada"
            }
            closeAddEditSheet()
        }
    }

    fun quickResolveBet(bet: BetEntity, newStatus: String) {
        viewModelScope.launch {
            val payout = when (newStatus) {
                "WON" -> bet.stake * bet.odds
                "LOST" -> 0.0
                "REFUNDED" -> bet.stake
                else -> 0.0
            }
            val updated = bet.copy(status = newStatus, payout = payout)
            repository.updateBet(updated)
            _messageSnackbar.value = "Estado actualizado a ${
                when (newStatus) {
                    "WON" -> "Ganada (+${uiState.value.currency.symbol}${payout})"
                    "LOST" -> "Perdida (-${uiState.value.currency.symbol}${bet.stake})"
                    "REFUNDED" -> "Reembolso"
                    else -> newStatus
                }
            }"
        }
    }

    fun deleteBet(bet: BetEntity) {
        viewModelScope.launch {
            repository.deleteBetById(bet.id)
            _messageSnackbar.value = "Registro eliminado"
        }
    }

    fun saveRiskSettings(startingBankroll: Double, weeklyLossLimit: Double, weeklyStakeLimit: Double) {
        viewModelScope.launch {
            val curr = uiState.value.currency.code
            repository.saveRiskSettings(
                RiskSettingsEntity(
                    id = 1,
                    startingBankroll = startingBankroll,
                    weeklyLossLimit = weeklyLossLimit,
                    weeklyStakeLimit = weeklyStakeLimit,
                    currencyCode = curr
                )
            )
            _isRiskSettingsOpen.value = false
            _messageSnackbar.value = "Límites y bankroll actualizados"
        }
    }

    fun openEmergencyResetDialog() {
        _isEmergencyResetOpen.value = true
    }

    fun closeEmergencyResetDialog() {
        _isEmergencyResetOpen.value = false
    }

    fun confirmEmergencyReset() {
        viewModelScope.launch {
            repository.wipeAllData()
            _selectedPeriod.value = PeriodFilter.THIS_WEEK
            _selectedCategory.value = CategoryFilter.ALL
            _selectedStatus.value = StatusFilter.ALL
            _searchQuery.value = ""
            _isEmergencyResetOpen.value = false
            _messageSnackbar.value = "⚠️ Todos los datos han sido borrados permanentemente. Base de datos reiniciada a cero."
        }
    }

    fun clearSnackbar() {
        _messageSnackbar.value = null
    }
}
