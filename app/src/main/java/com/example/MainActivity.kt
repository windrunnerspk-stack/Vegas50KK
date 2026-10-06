package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddEditBetSheet
import com.example.ui.components.AuthAndProfileDialog
import com.example.ui.components.AuthScreen
import com.example.ui.components.BankrollChart
import com.example.ui.components.BetItemCard
import com.example.ui.components.CategoryComparison
import com.example.ui.components.CurrencySelectorDialog
import com.example.ui.components.EmergencyResetDialog
import com.example.ui.components.KpiMetricsGrid
import com.example.ui.components.RiskControlMeter
import com.example.ui.components.RiskSettingsDialog
import com.example.ui.components.VegasHeader
import com.example.ui.theme.Vegas50kTheme
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasBorderSubtle
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurface
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import com.example.ui.viewmodel.CategoryFilter
import com.example.ui.viewmodel.StatusFilter
import com.example.ui.viewmodel.VegasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vegas50kTheme {
                VegasApp()
            }
        }
    }
}

@Composable
fun VegasApp(
    viewModel: VegasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle global snackbar messages
    LaunchedEffect(uiState.messageSnackbar) {
        uiState.messageSnackbar?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    val isUserAuthenticated = uiState.activeUser != null || uiState.isGuestSession

    if (!isUserAuthenticated) {
        // Initial Screen: Login and Registration with Firebase Auth
        Box(modifier = Modifier.fillMaxSize()) {
            AuthScreen(
                currentCurrency = uiState.currency,
                onSignInWithGoogle = { activityContext, onFinished ->
                    viewModel.signInWithGoogle(activityContext, onFinished)
                },
                onContinueAsGuest = {
                    viewModel.continueAsGuest()
                }
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
            )
        }
    } else {
        // Main Dashboard Screen
        VegasDashboardScreen(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VegasDashboardScreen(
    viewModel: VegasViewModel,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VegasBg),
        containerColor = VegasBg,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddBetSheet(null) },
                containerColor = VegasGoldPrimary,
                contentColor = VegasBg,
                shape = CircleShape,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("fab_add_bet")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar nueva apuesta",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VegasGoldPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Header with branding, currency, profile, period selector, and bankroll balance
                item {
                    VegasHeader(
                        kpi = uiState.kpis,
                        currency = uiState.currency,
                        activeUser = uiState.activeUser,
                        selectedPeriod = uiState.selectedPeriod,
                        onPeriodSelected = { viewModel.setPeriod(it) },
                        onAddBetClick = { viewModel.openAddBetSheet(null) },
                        onOpenRiskSettings = { viewModel.openRiskSettings() },
                        onOpenCurrencySelector = { viewModel.openCurrencyDialog() },
                        onOpenAuthProfile = { viewModel.openAuthDialog() },
                        onOpenEmergencyReset = { viewModel.openEmergencyResetDialog() }
                    )
                }

                // 2. Key Metrics & KPIs Cards
                item {
                    KpiMetricsGrid(
                        kpi = uiState.kpis,
                        currency = uiState.currency
                    )
                }

                // 3. Risk Management & Stop Loss Meter
                item {
                    RiskControlMeter(
                        kpi = uiState.kpis,
                        currency = uiState.currency,
                        onAdjustLimitsClick = { viewModel.openRiskSettings() }
                    )
                }

                // 4. Bankroll Evolution Chart (P&L over time)
                item {
                    BankrollChart(
                        points = uiState.chartPoints,
                        startingBankroll = uiState.kpis.startingBankroll,
                        currency = uiState.currency
                    )
                }

                // 5. Category Performance: Sports vs Casino
                item {
                    CategoryComparison(
                        stats = uiState.categoryStats,
                        currency = uiState.currency
                    )
                }

                // 6. Movements History: Search & Filters Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HISTORIAL DE MOVIMIENTOS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = VegasTextPrimary,
                                letterSpacing = 0.5.sp
                            )

                            Surface(
                                color = Color(0xFF1B2436),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "${uiState.filteredBets.size} registros",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VegasGoldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Buscar evento, equipo, mesa o mercado...", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = VegasTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpiar búsqueda",
                                            tint = VegasTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_bets_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VegasGoldPrimary,
                                unfocusedBorderColor = VegasBorder,
                                focusedTextColor = VegasTextPrimary,
                                unfocusedTextColor = VegasTextPrimary,
                                cursorColor = VegasGoldPrimary,
                                focusedContainerColor = VegasSurface,
                                unfocusedContainerColor = VegasSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryFilter.values().forEach { cat ->
                                val isSelected = uiState.selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VegasGoldPrimary else VegasSurfaceCard)
                                        .border(1.dp, if (isSelected) VegasGoldPrimary else VegasBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setCategory(cat) }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                        .testTag("filter_cat_${cat.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) VegasBg else VegasTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Filter Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusFilter.values().forEach { st ->
                                val isSelected = uiState.selectedStatus == st
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { viewModel.setStatus(st) }
                                        .testTag("filter_status_${st.name.lowercase()}"),
                                    color = if (isSelected) Color(0xFF291F0A) else Color(0xFF141C2B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) VegasGoldPrimary else VegasBorderSubtle
                                    )
                                ) {
                                    Text(
                                        text = st.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) VegasGoldLight else VegasTextMuted,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 7. Bet List Items
                if (uiState.filteredBets.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VegasSurfaceCard)
                                .border(1.dp, VegasBorder, RoundedCornerShape(16.dp))
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🎲",
                                    fontSize = 36.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No se encontraron movimientos",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VegasTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Prueba cambiando los filtros o registra una nueva apuesta.",
                                    fontSize = 11.sp,
                                    color = VegasTextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.filteredBets,
                        key = { it.id }
                    ) { bet ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            BetItemCard(
                                bet = bet,
                                currency = uiState.currency,
                                onEditClick = { viewModel.openAddBetSheet(bet) },
                                onDeleteClick = { viewModel.deleteBet(bet) },
                                onQuickResolve = { newStatus -> viewModel.quickResolveBet(bet, newStatus) }
                            )
                        }
                    }
                }

                // Bottom spacer
                item {
                    Spacer(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .height(80.dp)
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Adding/Editing a Bet
    if (uiState.isAddEditSheetOpen) {
        AddEditBetSheet(
            sheetState = sheetState,
            betToEdit = uiState.editingBet,
            currency = uiState.currency,
            onDismiss = { viewModel.closeAddEditSheet() },
            onSaveBet = { id, cat, subcat, evName, mkt, odds, stake, st, payout, notes ->
                viewModel.saveBet(id, cat, subcat, evName, mkt, odds, stake, st, payout, notes)
            }
        )
    }

    // Risk Settings Dialog
    if (uiState.isRiskSettingsOpen) {
        RiskSettingsDialog(
            initialStartingBankroll = uiState.kpis.startingBankroll,
            initialWeeklyLossLimit = uiState.kpis.weeklyLossLimit,
            initialWeeklyStakeLimit = uiState.kpis.weeklyStakeLimit,
            currency = uiState.currency,
            onDismiss = { viewModel.closeRiskSettings() },
            onSaveSettings = { bankroll, lossLimit, stakeLimit ->
                viewModel.saveRiskSettings(bankroll, lossLimit, stakeLimit)
            }
        )
    }

    // Currency Selector Dialog
    if (uiState.isCurrencyDialogOpen) {
        CurrencySelectorDialog(
            selectedCurrency = uiState.currency,
            onCurrencySelected = { viewModel.setCurrency(it) },
            onDismiss = { viewModel.closeCurrencyDialog() }
        )
    }

    // Auth & Profile Dialog (accessible from inside Dashboard)
    if (uiState.isAuthDialogOpen) {
        AuthAndProfileDialog(
            activeUser = uiState.activeUser,
            currentCurrency = uiState.currency,
            onDismiss = { viewModel.closeAuthDialog() },
            onOpenCurrencyDialog = { viewModel.openCurrencyDialog() },
            onSignInWithGoogle = { activityContext, onFinished ->
                viewModel.signInWithGoogle(activityContext, onFinished)
            },
            onLogoutUser = { viewModel.logoutUser() },
            onOpenEmergencyReset = { viewModel.openEmergencyResetDialog() }
        )
    }

    // Emergency Reset Dialog (Ventana de Emergencia)
    if (uiState.isEmergencyResetOpen) {
        EmergencyResetDialog(
            onDismiss = { viewModel.closeEmergencyResetDialog() },
            onConfirmReset = { viewModel.confirmEmergencyReset() }
        )
    }
}
