package com.example.ui.viewmodel

import com.example.data.local.AppCurrency
import com.example.data.local.BetEntity
import com.example.data.local.UserAccountEntity

enum class PeriodFilter(val title: String) {
    THIS_WEEK("Esta Semana"),
    THIS_MONTH("Este Mes"),
    ALL_TIME("Histórico Global")
}

enum class CategoryFilter(val title: String) {
    ALL("Todos"),
    SPORTS("Deportes"),
    CASINO("Casino")
}

enum class StatusFilter(val title: String) {
    ALL("Todos"),
    WON("Ganadas"),
    LOST("Perdidas"),
    PENDING("Pendientes"),
    REFUNDED("Reembolsos")
}

enum class RiskStatus {
    SAFE,       // < 70%
    WARNING,    // 70% - 90%
    CRITICAL    // > 90%
}

data class KpiSummary(
    val currentBankroll: Double = 50000.0,
    val startingBankroll: Double = 50000.0,
    val periodStake: Double = 0.0,
    val periodGrossProfit: Double = 0.0,
    val periodLosses: Double = 0.0,
    val periodNetProfit: Double = 0.0,
    val periodRoi: Double = 0.0,
    val winRate: Double = 0.0,
    val totalBetsCount: Int = 0,
    val wonCount: Int = 0,
    val lostCount: Int = 0,
    val pendingCount: Int = 0,
    val refundedCount: Int = 0,
    val weeklyLossUsed: Double = 0.0,
    val weeklyStakeUsed: Double = 0.0,
    val weeklyLossLimit: Double = 3500.0,
    val weeklyStakeLimit: Double = 9000.0,
    val riskScorePercent: Float = 0f,
    val riskStatus: RiskStatus = RiskStatus.SAFE,
    val currentStreak: Int = 0,
    val biggestWin: Double = 0.0,
    val averageOdds: Double = 0.0
)

data class ChartDataPoint(
    val label: String,
    val bankrollValue: Double,
    val netChange: Double,
    val timestamp: Long
)

data class CategoryStats(
    val sportsStake: Double = 0.0,
    val sportsReturn: Double = 0.0,
    val sportsNetProfit: Double = 0.0,
    val sportsWinRate: Double = 0.0,
    val sportsCount: Int = 0,
    val casinoStake: Double = 0.0,
    val casinoReturn: Double = 0.0,
    val casinoNetProfit: Double = 0.0,
    val casinoWinRate: Double = 0.0,
    val casinoCount: Int = 0
)

data class VegasUiState(
    val isLoading: Boolean = false,
    val allBets: List<BetEntity> = emptyList(),
    val filteredBets: List<BetEntity> = emptyList(),
    val kpis: KpiSummary = KpiSummary(),
    val chartPoints: List<ChartDataPoint> = emptyList(),
    val categoryStats: CategoryStats = CategoryStats(),
    val selectedPeriod: PeriodFilter = PeriodFilter.THIS_WEEK,
    val selectedCategory: CategoryFilter = CategoryFilter.ALL,
    val selectedStatus: StatusFilter = StatusFilter.ALL,
    val searchQuery: String = "",
    val currency: AppCurrency = AppCurrency.USD,
    val activeUser: UserAccountEntity? = null,
    val editingBet: BetEntity? = null,
    val isAddEditSheetOpen: Boolean = false,
    val isRiskSettingsOpen: Boolean = false,
    val isCurrencyDialogOpen: Boolean = false,
    val isAuthDialogOpen: Boolean = false,
    val isEmergencyResetOpen: Boolean = false,
    val isGuestSession: Boolean = false,
    val messageSnackbar: String? = null
)
