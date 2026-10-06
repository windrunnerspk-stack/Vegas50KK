package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.data.local.UserAccountEntity
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasBorderSubtle
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldMetallic
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurface
import com.example.ui.theme.VegasSurfaceElevated
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import com.example.ui.viewmodel.KpiSummary
import com.example.ui.viewmodel.PeriodFilter
import java.util.Locale

@Composable
fun VegasHeader(
    kpi: KpiSummary,
    currency: AppCurrency,
    activeUser: UserAccountEntity?,
    selectedPeriod: PeriodFilter,
    onPeriodSelected: (PeriodFilter) -> Unit,
    onAddBetClick: () -> Unit,
    onOpenRiskSettings: () -> Unit,
    onOpenCurrencySelector: () -> Unit,
    onOpenAuthProfile: () -> Unit,
    onResetDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Branding & Controls Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimalist Casino Chip Logo
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF26334D), Color(0xFF0F1522), Color(0xFF070A10))
                            )
                        )
                        .border(2.dp, VegasGoldMetallic, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, VegasGoldLight.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "50K",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasGoldMetallic,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VEGAS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "50K",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasGoldMetallic,
                            letterSpacing = 1.sp
                        )
                    }

                    // VIP Badge with Currency selector trigger
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF2A200B),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, VegasGoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "VIP DASHBOARD",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegasGoldLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Interactive Currency Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF141C2B))
                                .border(0.5.dp, VegasBorder, RoundedCornerShape(4.dp))
                                .clickable { onOpenCurrencySelector() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("currency_selector_chip")
                        ) {
                            Text(
                                text = "${currency.flag} ${currency.code}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = VegasGoldMetallic
                            )
                        }
                    }
                }
            }

            // Quick Actions: Currency, Profile, Risk Settings
            Row(verticalAlignment = Alignment.CenterVertically) {
                // User Account / Profile Button
                IconButton(
                    onClick = onOpenAuthProfile,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (activeUser != null) Color(0xFF2B200B) else VegasSurfaceElevated)
                        .border(1.dp, if (activeUser != null) VegasGoldPrimary else VegasBorder, CircleShape)
                        .testTag("user_profile_button")
                ) {
                    if (activeUser != null) {
                        val initials = activeUser.displayName.split(" ")
                            .mapNotNull { it.firstOrNull() }
                            .take(2)
                            .joinToString("")
                            .uppercase()
                        Text(
                            text = initials.ifBlank { "VIP" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasGoldLight
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Iniciar sesión o registrarse",
                            tint = VegasTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onOpenRiskSettings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(VegasSurfaceElevated)
                        .border(1.dp, VegasBorder, CircleShape)
                        .testTag("risk_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Configuración de riesgo",
                        tint = VegasGoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onResetDemoData,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(VegasSurfaceElevated)
                        .border(1.dp, VegasBorder, CircleShape)
                        .testTag("reset_demo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restablecer datos demo",
                        tint = VegasTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prominent Bankroll Balance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = VegasGoldMetallic, spotColor = VegasGoldPrimary)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B2438), Color(0xFF131A29), Color(0xFF0C111C))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(VegasGoldMetallic.copy(alpha = 0.6f), VegasBorder, VegasEmerald.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BALANCE DISPONIBLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasTextSecondary,
                        letterSpacing = 1.sp
                    )

                    // Currency & Tier tag
                    Surface(
                        modifier = Modifier.clickable { onOpenCurrencySelector() },
                        color = Color(0xFF1E283C),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorderSubtle)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${currency.flag} ${currency.code}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegasGoldLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "• TIER VIP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VegasTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Big Bankroll Numbers formatted with selected currency
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = currency.symbol,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasGoldMetallic,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(Locale.US, "%,.2f", kpi.currentBankroll),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Net change comparison from initial bankroll
                val totalPnl = kpi.currentBankroll - kpi.startingBankroll
                val totalPnlPct = if (kpi.startingBankroll > 0) (totalPnl / kpi.startingBankroll) * 100.0 else 0.0
                val isPositive = totalPnl >= 0

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isPositive) Color(0xFF07291B) else Color(0xFF331015))
                            .border(
                                0.5.dp,
                                if (isPositive) VegasEmerald.copy(alpha = 0.5f) else VegasCrimson.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${if (isPositive) "+" else ""}${currency.symbol}${String.format(Locale.US, "%,.2f", totalPnl)} (${if (isPositive) "+" else ""}${String.format(Locale.US, "%.1f", totalPnlPct)}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) VegasEmerald else VegasCrimson
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "vs. Base (${currency.symbol}${String.format(Locale.US, "%,.0f", kpi.startingBankroll)})",
                        fontSize = 11.sp,
                        color = VegasTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Quick Action: Register Bet
                Button(
                    onClick = onAddBetClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("register_bet_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VegasGoldPrimary,
                        contentColor = VegasBg
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = VegasBg
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REGISTRAR APUESTA / MOVIMIENTO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Period Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VegasSurface)
                .border(1.dp, VegasBorder, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PeriodFilter.values().forEach { period ->
                val isSelected = period == selectedPeriod
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) VegasGoldPrimary else Color.Transparent,
                    label = "tabBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) VegasBg else VegasTextSecondary,
                    label = "tabText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 10.dp)
                        .testTag("period_tab_${period.name.lowercase(Locale.ROOT)}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period.title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
