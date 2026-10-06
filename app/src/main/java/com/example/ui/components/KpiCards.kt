package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.ui.theme.VegasAmber
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasCrimsonLight
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasEmeraldBright
import com.example.ui.theme.VegasEmeraldLight
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import com.example.ui.viewmodel.KpiSummary
import java.util.Locale

@Composable
fun KpiMetricsGrid(
    kpi: KpiSummary,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Inversión (Stake) & Beneficio Neto / ROI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Inversión del Periodo
            val stakeRatio = if (kpi.weeklyStakeLimit > 0) (kpi.periodStake / kpi.weeklyStakeLimit).toFloat() else 0f
            val animatedStakeRatio by animateFloatAsState(targetValue = stakeRatio.coerceIn(0f, 1f), label = "stakeProg")

            KpiCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_investment_card"),
                title = "INVERSIÓN TOTAL",
                value = currency.format(kpi.periodStake),
                icon = Icons.Default.Payments,
                iconTint = VegasGoldPrimary,
                accentColor = VegasGoldPrimary
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tope ${currency.formatNoDecimals(kpi.weeklyStakeLimit)}",
                            fontSize = 10.sp,
                            color = VegasTextMuted
                        )
                        Text(
                            text = "${(stakeRatio * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (stakeRatio > 0.9f) VegasCrimson else VegasGoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { animatedStakeRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (stakeRatio > 0.9f) VegasCrimson else VegasGoldPrimary,
                        trackColor = Color(0xFF1E283A)
                    )
                }
            }

            // Card 2: Beneficio Neto / ROI
            val isNetPositive = kpi.periodNetProfit >= 0
            val netColor = if (isNetPositive) VegasEmerald else VegasCrimson
            val netSign = if (isNetPositive) "+" else ""

            KpiCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_net_profit_card"),
                title = "BENEFICIO NETO",
                value = "$netSign${currency.format(kpi.periodNetProfit)}",
                valueColor = netColor,
                icon = if (isNetPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                iconTint = netColor,
                accentColor = netColor
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isNetPositive) Color(0xFF092E1F) else Color(0xFF381014))
                            .border(
                                0.5.dp,
                                if (isNetPositive) VegasEmerald.copy(alpha = 0.5f) else VegasCrimson.copy(alpha = 0.5f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$netSign${String.format(Locale.US, "%.1f", kpi.periodRoi)}% ROI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isNetPositive) VegasEmeraldLight else VegasCrimsonLight
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retorno s/stake",
                        fontSize = 10.sp,
                        color = VegasTextMuted
                    )
                }
            }
        }

        // Row 2: Total Ingresos & Total Egresos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 3: Total Ingresos (Cobrado)
            KpiCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_gross_income_card"),
                title = "TOTAL INGRESOS",
                value = currency.format(kpi.periodGrossProfit),
                valueColor = VegasEmeraldBright,
                icon = Icons.Default.TrendingUp,
                iconTint = VegasEmerald,
                accentColor = VegasEmerald
            ) {
                Text(
                    text = "${kpi.wonCount} apuestas ganadas",
                    fontSize = 10.sp,
                    color = VegasTextMuted
                )
            }

            // Card 4: Total Egresos (Pérdidas)
            KpiCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_losses_card"),
                title = "TOTAL EGRESOS",
                value = "-${currency.format(kpi.periodLosses)}",
                valueColor = VegasCrimson,
                icon = Icons.Default.TrendingDown,
                iconTint = VegasCrimson,
                accentColor = VegasCrimson
            ) {
                Text(
                    text = "${kpi.lostCount} fallos directos",
                    fontSize = 10.sp,
                    color = VegasTextMuted
                )
            }
        }

        // Row 3: Win Rate & Desglose de Resultados
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VegasSurfaceCard)
                .border(1.dp, VegasBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
                .testTag("kpi_win_rate_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Circular Progress for Win Rate
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val animatedWinRate by animateFloatAsState(
                        targetValue = (kpi.winRate / 100.0).toFloat().coerceIn(0f, 1f),
                        label = "winRateProg"
                    )

                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedWinRate },
                            modifier = Modifier.size(52.dp),
                            color = if (kpi.winRate >= 55.0) VegasEmerald else VegasAmber,
                            strokeWidth = 5.dp,
                            trackColor = Color(0xFF1B2436)
                        )
                        Text(
                            text = "${kpi.winRate.toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "WIN RATE GLOBAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegasTextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${kpi.wonCount} Acertadas / ${kpi.wonCount + kpi.lostCount} Resueltas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VegasTextPrimary
                        )
                        if (kpi.pendingCount > 0) {
                            Text(
                                text = "${kpi.pendingCount} apuestas en juego (pendientes)",
                                fontSize = 11.sp,
                                color = VegasGoldLight
                            )
                        }
                    }
                }

                // Right: Mini Streak or Average Odds
                Column(horizontalAlignment = Alignment.End) {
                    val streak = kpi.currentStreak
                    if (streak != 0) {
                        val isWinStreak = streak > 0
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isWinStreak) Color(0xFF072B1C) else Color(0xFF381216))
                                .border(
                                    0.5.dp,
                                    if (isWinStreak) VegasEmerald.copy(alpha = 0.5f) else VegasCrimson.copy(alpha = 0.5f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isWinStreak) "🔥 $streak Rachas W" else "❄️ ${-streak} Rachas L",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWinStreak) VegasEmeraldBright else VegasCrimson
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cuota media: ${String.format(Locale.US, "x%.2f", kpi.averageOdds)}",
                        fontSize = 10.sp,
                        color = VegasTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    valueColor: Color = VegasTextPrimary,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(VegasSurfaceCard)
            .border(1.dp, VegasBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = VegasTextSecondary,
                    letterSpacing = 0.5.sp
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = valueColor,
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}
