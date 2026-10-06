package com.example.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.data.local.BetEntity
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasBorderSubtle
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasCrimsonLight
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasEmeraldBright
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldMetallic
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BetItemCard(
    bet: BetEntity,
    currency: AppCurrency,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onQuickResolve: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    // Category emoji & colors
    val (catIcon, catColor) = when (bet.subcategory.lowercase(Locale.ROOT)) {
        "fútbol", "futbol" -> "⚽" to Color(0xFF38BDF8)
        "baloncesto" -> "🏀" to Color(0xFFF97316)
        "tenis" -> "🎾" to Color(0xFFA3E635)
        "ruleta" -> "🎡" to VegasGoldMetallic
        "blackjack" -> "🃏" to Color(0xFFA855F7)
        "slots" -> "🎰" to Color(0xFFEC4899)
        "poker" -> "♠️" to Color(0xFFE2E8F0)
        "baccarat" -> "💎" to Color(0xFF2DD4BF)
        else -> if (bet.category == "SPORTS") "🏆" to Color(0xFF38BDF8) else "🎲" to VegasGoldMetallic
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VegasSurfaceCard)
            .border(1.dp, VegasBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("bet_item_${bet.id}")
    ) {
        Column {
            // Row 1: Category, Subcategory badge, Status badge & More Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Icon Circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E273A))
                            .border(0.5.dp, VegasBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = catIcon, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Subcategory badge
                    Surface(
                        color = Color(0xFF1C2436),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, catColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = bet.subcategory.uppercase(Locale.ROOT),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = catColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = dateFormat.format(Date(bet.timestamp)),
                        fontSize = 10.sp,
                        color = VegasTextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Badge
                    StatusBadge(bet = bet)

                    Spacer(modifier = Modifier.width(4.dp))

                    // More actions menu
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = VegasTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(VegasBg).border(1.dp, VegasBorder)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar apuesta", color = VegasTextPrimary) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = VegasGoldPrimary) },
                                onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = VegasCrimsonLight) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = VegasCrimson) },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Event Name & Market
            Text(
                text = bet.eventName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VegasTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = bet.market,
                fontSize = 12.sp,
                color = VegasTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Odds, Stake and Return details formatted with selected currency
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1522))
                    .border(0.5.dp, VegasBorderSubtle, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cuota
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Cuota:", fontSize = 11.sp, color = VegasTextMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(Locale.US, "x%.2f", bet.odds),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasGoldMetallic
                    )
                }

                // Stake
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Stake:", fontSize = 11.sp, color = VegasTextMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currency.format(bet.stake),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasTextPrimary
                    )
                }

                // PnL / Cobro
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (bet.status) {
                        "WON" -> {
                            Text(text = "Neto: ", fontSize = 11.sp, color = VegasTextMuted)
                            Text(
                                text = "+${currency.format(bet.netProfit)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = VegasEmeraldBright
                            )
                        }
                        "LOST" -> {
                            Text(text = "Pérdida: ", fontSize = 11.sp, color = VegasTextMuted)
                            Text(
                                text = "-${currency.format(bet.stake)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = VegasCrimson
                            )
                        }
                        "PENDING" -> {
                            val potentialWin = (bet.stake * bet.odds) - bet.stake
                            Text(text = "Posible: ", fontSize = 11.sp, color = VegasTextMuted)
                            Text(
                                text = "+${currency.format(potentialWin)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegasGoldLight
                            )
                        }
                        "REFUNDED" -> {
                            Text(
                                text = "Reembolso ${currency.format(bet.stake)}",
                                fontSize = 11.sp,
                                color = VegasTextSecondary
                            )
                        }
                    }
                }
            }

            // Quick Actions if bet is PENDING
            if (bet.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onQuickResolve("WON") },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF092E1E),
                            contentColor = VegasEmeraldBright
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasEmerald.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ganó", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onQuickResolve("LOST") },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF381014),
                            contentColor = VegasCrimsonLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasCrimson.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Perdió", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Notes if available
            if (bet.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nota: ${bet.notes}",
                    fontSize = 11.sp,
                    color = VegasTextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(bet: BetEntity) {
    val (text, bg, border, textColor) = when (bet.status) {
        "WON" -> Quad("GANADA", Color(0xFF092B1C), VegasEmerald, VegasEmeraldBright)
        "LOST" -> Quad("PERDIDA", Color(0xFF381115), VegasCrimson, VegasCrimsonLight)
        "PENDING" -> Quad("PENDIENTE", Color(0xFF2E2209), VegasGoldPrimary, VegasGoldLight)
        else -> Quad("REEMBOLSO", Color(0xFF1E2533), VegasBorder, VegasTextSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(0.5.dp, border.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
