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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.SportsSoccer
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
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldMetallic
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import com.example.ui.viewmodel.CategoryStats

@Composable
fun CategoryComparison(
    stats: CategoryStats,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    val totalStake = stats.sportsStake + stats.casinoStake
    val sportsShare = if (totalStake > 0) (stats.sportsStake / totalStake).toFloat() else 0.5f
    val casinoShare = 1f - sportsShare

    val animatedSportsShare by animateFloatAsState(targetValue = sportsShare, label = "sportsShare")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VegasSurfaceCard)
            .border(1.dp, VegasBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("category_comparison_section")
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RENDIMIENTO: DEPORTES VS CASINO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Comparativa en ${currency.code}",
                        fontSize = 10.sp,
                        color = VegasTextMuted
                    )
                }

                Text(
                    text = "${stats.sportsCount + stats.casinoCount} operaciones",
                    fontSize = 10.sp,
                    color = VegasGoldLight,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Proportional Distribution Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Deportes (${(sportsShare * 100).toInt()}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VegasTextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Casino (${(casinoShare * 100).toInt()}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VegasTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VegasGoldMetallic))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dual progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF1E283C))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(animatedSportsShare.coerceAtLeast(0.01f))
                            .height(10.dp)
                            .background(Color(0xFF38BDF8))
                    )
                    Box(
                        modifier = Modifier
                            .weight((1f - animatedSportsShare).coerceAtLeast(0.01f))
                            .height(10.dp)
                            .background(VegasGoldMetallic)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two-column side by side comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left: Deportes
                CategoryDetailCard(
                    modifier = Modifier.weight(1f),
                    title = "DEPORTES",
                    icon = Icons.Default.SportsSoccer,
                    accentColor = Color(0xFF38BDF8),
                    stake = stats.sportsStake,
                    payout = stats.sportsReturn,
                    net = stats.sportsNetProfit,
                    winRate = stats.sportsWinRate,
                    count = stats.sportsCount,
                    currency = currency
                )

                // Right: Casino
                CategoryDetailCard(
                    modifier = Modifier.weight(1f),
                    title = "CASINO",
                    icon = Icons.Default.Casino,
                    accentColor = VegasGoldMetallic,
                    stake = stats.casinoStake,
                    payout = stats.casinoReturn,
                    net = stats.casinoNetProfit,
                    winRate = stats.casinoWinRate,
                    count = stats.casinoCount,
                    currency = currency
                )
            }
        }
    }
}

@Composable
private fun CategoryDetailCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    stake: Double,
    payout: Double,
    net: Double,
    winRate: Double,
    count: Int,
    currency: AppCurrency,
    modifier: Modifier = Modifier
) {
    val isNetPos = net >= 0
    val netSign = if (isNetPos) "+" else ""
    val netColor = if (isNetPos) VegasEmerald else VegasCrimson

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1522))
            .border(1.dp, VegasBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )

                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Net Profit
            Text(
                text = "Beneficio Neto",
                fontSize = 10.sp,
                color = VegasTextMuted
            )
            Text(
                text = "$netSign${currency.format(net)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = netColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Win Rate & Stake
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Win Rate", fontSize = 9.sp, color = VegasTextMuted)
                    Text(
                        text = "${winRate.toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Stake Total", fontSize = 9.sp, color = VegasTextMuted)
                    Text(
                        text = currency.formatNoDecimals(stake),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasTextPrimary
                    )
                }
            }
        }
    }
}
