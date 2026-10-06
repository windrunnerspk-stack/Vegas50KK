package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.ui.theme.VegasAmber
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasCrimsonBright
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import com.example.ui.viewmodel.KpiSummary
import com.example.ui.viewmodel.RiskStatus

@Composable
fun RiskControlMeter(
    kpi: KpiSummary,
    currency: AppCurrency,
    onAdjustLimitsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lossRatio = if (kpi.weeklyLossLimit > 0) (kpi.weeklyLossUsed / kpi.weeklyLossLimit).toFloat() else 0f
    val stakeRatio = if (kpi.weeklyStakeLimit > 0) (kpi.weeklyStakeUsed / kpi.weeklyStakeLimit).toFloat() else 0f
    val maxRatio = maxOf(lossRatio, stakeRatio)

    val animatedRiskRatio by animateFloatAsState(targetValue = maxRatio.coerceIn(0f, 1.2f), label = "riskMeter")

    val statusColor by animateColorAsState(
        targetValue = when (kpi.riskStatus) {
            RiskStatus.SAFE -> VegasEmerald
            RiskStatus.WARNING -> VegasAmber
            RiskStatus.CRITICAL -> VegasCrimsonBright
        },
        label = "statusColor"
    )

    val statusBgColor by animateColorAsState(
        targetValue = when (kpi.riskStatus) {
            RiskStatus.SAFE -> Color(0xFF09291B)
            RiskStatus.WARNING -> Color(0xFF2B200A)
            RiskStatus.CRITICAL -> Color(0xFF381014)
        },
        label = "statusBgColor"
    )

    val statusText = when (kpi.riskStatus) {
        RiskStatus.SAFE -> "RIESGO BAJO • DENTRO DE LÍMITES"
        RiskStatus.WARNING -> "PRECAUCIÓN • CERCA DEL TOPE"
        RiskStatus.CRITICAL -> "ALERTA CRÍTICA • LÍMITE EXCEDIDO"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VegasSurfaceCard)
            .border(1.dp, VegasBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("risk_control_section")
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (kpi.riskStatus == RiskStatus.CRITICAL) Icons.Default.Warning else Icons.Default.Shield,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "CONTROL DE RIESGO SEMANAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegasTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Protección de Bankroll y Stop Loss",
                            fontSize = 10.sp,
                            color = VegasTextMuted
                        )
                    }
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBgColor)
                        .border(0.5.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Composite Visual Risk Gauge Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Exposición Total de Riesgo",
                        fontSize = 11.sp,
                        color = VegasTextSecondary
                    )
                    Text(
                        text = "${(maxRatio * 100).toInt()}% utilizado",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress with colored threshold zones
                LinearProgressIndicator(
                    progress = { animatedRiskRatio.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = statusColor,
                    trackColor = Color(0xFF1E283C)
                )

                // Scale ticks: 0% | 70% | 90% | 100%
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0% Normal", fontSize = 9.sp, color = VegasTextMuted)
                    Text(text = "70% Alerta", fontSize = 9.sp, color = VegasAmber)
                    Text(text = "90%+ Peligro", fontSize = 9.sp, color = VegasCrimson)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two-column detailed breakdown: Loss Limit & Stake Limit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Tope Semanal de Pérdidas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VegasBg)
                        .border(0.5.dp, VegasBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Tope Semanal Pérdidas",
                            fontSize = 10.sp,
                            color = VegasTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currency.formatNoDecimals(kpi.weeklyLossUsed)} / ${currency.formatNoDecimals(kpi.weeklyLossLimit)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (lossRatio > 0.9f) VegasCrimson else VegasTextPrimary
                        )
                        val lossRemaining = (kpi.weeklyLossLimit - kpi.weeklyLossUsed).coerceAtLeast(0.0)
                        Text(
                            text = "Margen: ${currency.formatNoDecimals(lossRemaining)}",
                            fontSize = 10.sp,
                            color = if (lossRemaining == 0.0) VegasCrimson else VegasTextMuted
                        )
                    }
                }

                // Metric 2: Límite Máximo Apostado por Semana
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VegasBg)
                        .border(0.5.dp, VegasBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Tope Semanal Stake",
                            fontSize = 10.sp,
                            color = VegasTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currency.formatNoDecimals(kpi.weeklyStakeUsed)} / ${currency.formatNoDecimals(kpi.weeklyStakeLimit)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (stakeRatio > 0.9f) VegasCrimson else VegasTextPrimary
                        )
                        val stakeRemaining = (kpi.weeklyStakeLimit - kpi.weeklyStakeUsed).coerceAtLeast(0.0)
                        Text(
                            text = "Disponible: ${currency.formatNoDecimals(stakeRemaining)}",
                            fontSize = 10.sp,
                            color = if (stakeRemaining == 0.0) VegasCrimson else VegasTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button to adjust limits
            OutlinedButton(
                onClick = onAdjustLimitsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("adjust_limits_button"),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VegasGoldPrimary.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VegasGoldLight
                )
            ) {
                Text(
                    text = "Ajustar Límites y Parámetros de Riesgo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
