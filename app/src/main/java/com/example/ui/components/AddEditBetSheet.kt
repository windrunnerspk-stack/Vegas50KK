package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.data.local.BetEntity
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditBetSheet(
    sheetState: SheetState,
    betToEdit: BetEntity?,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onSaveBet: (
        id: Long,
        category: String,
        subcategory: String,
        eventName: String,
        market: String,
        odds: Double,
        stake: Double,
        status: String,
        payout: Double,
        notes: String
    ) -> Unit
) {
    var category by remember { mutableStateOf(betToEdit?.category ?: "SPORTS") }
    var subcategory by remember {
        mutableStateOf(betToEdit?.subcategory ?: if (category == "SPORTS") "Fútbol" else "Ruleta")
    }
    var eventName by remember { mutableStateOf(betToEdit?.eventName ?: "") }
    var market by remember { mutableStateOf(betToEdit?.market ?: "") }
    var oddsStr by remember { mutableStateOf(betToEdit?.odds?.toString() ?: "2.00") }
    var stakeStr by remember { mutableStateOf(betToEdit?.stake?.let { String.format(Locale.US, "%.0f", it) } ?: "250") }
    var status by remember { mutableStateOf(betToEdit?.status ?: "PENDING") }
    var customPayoutStr by remember { mutableStateOf(betToEdit?.payout?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var notes by remember { mutableStateOf(betToEdit?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sportsPresets = listOf("Fútbol", "Baloncesto", "Tenis", "Béisbol", "MMA/UFC", "eSports")
    val casinoPresets = listOf("Ruleta", "Blackjack", "Slots", "Poker", "Baccarat", "Craps")

    val parsedOdds = oddsStr.toDoubleOrNull() ?: 1.0
    val parsedStake = stakeStr.toDoubleOrNull() ?: 0.0
    val calculatedPayout = parsedStake * parsedOdds
    val estimatedProfit = calculatedPayout - parsedStake

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VegasSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (betToEdit == null) "NUEVO MOVIMIENTO" else "EDITAR APUESTA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Valores calculados en ${currency.code} (${currency.symbol})",
                        fontSize = 11.sp,
                        color = VegasTextMuted
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VegasSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = VegasTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Category Switcher (Deportes vs Casino)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1522))
                    .border(1.dp, VegasBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val isSports = category == "SPORTS"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSports) VegasGoldPrimary else Color.Transparent)
                        .clickable {
                            category = "SPORTS"
                            subcategory = "Fútbol"
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚽ Deportes",
                        fontSize = 13.sp,
                        fontWeight = if (isSports) FontWeight.Black else FontWeight.Medium,
                        color = if (isSports) VegasBg else VegasTextSecondary
                    )
                }

                val isCasino = category == "CASINO"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCasino) VegasGoldPrimary else Color.Transparent)
                        .clickable {
                            category = "CASINO"
                            subcategory = "Ruleta"
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎰 Casino",
                        fontSize = 13.sp,
                        fontWeight = if (isCasino) FontWeight.Black else FontWeight.Medium,
                        color = if (isCasino) VegasBg else VegasTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subcategory Chips
            Text(
                text = "Disciplina o Juego Específico",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VegasTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = if (category == "SPORTS") sportsPresets else casinoPresets
                presets.forEach { item ->
                    val isSelected = subcategory == item
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { subcategory = item },
                        color = if (isSelected) Color(0xFF2C220B) else Color(0xFF161F2E),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VegasGoldPrimary else VegasBorder
                        )
                    ) {
                        Text(
                            text = item,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) VegasGoldLight else VegasTextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Event / Table Name Input
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text(if (category == "SPORTS") "Evento / Equipos (ej. Real Madrid vs City)" else "Mesa / Juego (ej. Ruleta Francesa VIP)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bet_event_input"),
                colors = outlinedColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Market / Bet Type Input
            OutlinedTextField(
                value = market,
                onValueChange = { market = it },
                label = { Text(if (category == "SPORTS") "Mercado (ej. Más de 2.5 goles)" else "Tipo de apuesta (ej. Pleno al 17)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bet_market_input"),
                colors = outlinedColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cuota & Stake Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = oddsStr,
                    onValueChange = { oddsStr = it },
                    label = { Text("Cuota (Odds)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bet_odds_input"),
                    colors = outlinedColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stakeStr,
                    onValueChange = { stakeStr = it },
                    label = { Text("Stake (${currency.symbol})") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bet_stake_input"),
                    colors = outlinedColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stake Add Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(50, 100, 250, 500, 1000).forEach { addAmount ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF141C2B))
                            .border(0.5.dp, VegasBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                val current = stakeStr.toDoubleOrNull() ?: 0.0
                                stakeStr = String.format(Locale.US, "%.0f", current + addAmount)
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${currency.symbol}$addAmount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegasGoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Projected Return Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F1522))
                    .border(1.dp, VegasBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Retorno Estimado (Cobro)", fontSize = 10.sp, color = VegasTextMuted)
                        Text(
                            text = currency.format(calculatedPayout),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasGoldMetallic
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Beneficio Neto Proyectado", fontSize = 10.sp, color = VegasTextMuted)
                        Text(
                            text = "+${currency.format(estimatedProfit)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Status Selector
            Text(
                text = "Estado Inicial del Pronóstico",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VegasTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("PENDING" to "Pendiente", "WON" to "Ganada", "LOST" to "Perdida", "REFUNDED" to "Reembolso").forEach { (code, label) ->
                    val isSel = status == code
                    val (activeBg, activeBorder, activeText) = when (code) {
                        "WON" -> Triple(Color(0xFF092E1E), VegasEmerald, VegasEmerald)
                        "LOST" -> Triple(Color(0xFF381014), VegasCrimson, VegasCrimson)
                        "PENDING" -> Triple(Color(0xFF2C2209), VegasGoldPrimary, VegasGoldLight)
                        else -> Triple(Color(0xFF1E2838), VegasBorder, VegasTextSecondary)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) activeBg else Color(0xFF121A28))
                            .border(1.dp, if (isSel) activeBorder else VegasBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                status = code
                                if (code == "WON" && customPayoutStr.isBlank()) {
                                    customPayoutStr = String.format(Locale.US, "%.2f", calculatedPayout)
                                }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                            color = if (isSel) activeText else VegasTextMuted
                        )
                    }
                }
            }

            if (status == "WON") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = customPayoutStr.ifBlank { String.format(Locale.US, "%.2f", calculatedPayout) },
                    onValueChange = { customPayoutStr = it },
                    label = { Text("Monto Real Cobrado (${currency.symbol})") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Strategy / Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Estrategia o Notas (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bet_notes_input"),
                colors = outlinedColors(),
                maxLines = 3
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    fontSize = 12.sp,
                    color = VegasCrimson,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (eventName.isBlank()) {
                        errorMessage = "Por favor ingresa el nombre del evento o mesa"
                        return@Button
                    }
                    if (market.isBlank()) {
                        errorMessage = "Por favor ingresa el mercado o jugada"
                        return@Button
                    }
                    if (parsedStake <= 0) {
                        errorMessage = "Ingresa un stake válido mayor a 0"
                        return@Button
                    }
                    if (parsedOdds <= 1.0) {
                        errorMessage = "La cuota debe ser mayor a 1.00"
                        return@Button
                    }

                    val finalPayout = when (status) {
                        "WON" -> customPayoutStr.toDoubleOrNull() ?: calculatedPayout
                        "LOST" -> 0.0
                        "REFUNDED" -> parsedStake
                        else -> 0.0
                    }

                    onSaveBet(
                        betToEdit?.id ?: 0L,
                        category,
                        subcategory,
                        eventName,
                        market,
                        parsedOdds,
                        parsedStake,
                        status,
                        finalPayout,
                        notes
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_bet_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VegasGoldPrimary,
                    contentColor = VegasBg
                )
            ) {
                Text(
                    text = if (betToEdit == null) "GUARDAR EN VEGAS 50K" else "ACTUALIZAR APUESTA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VegasGoldPrimary,
    unfocusedBorderColor = VegasBorder,
    focusedLabelColor = VegasGoldLight,
    unfocusedLabelColor = VegasTextSecondary,
    focusedTextColor = VegasTextPrimary,
    unfocusedTextColor = VegasTextPrimary,
    cursorColor = VegasGoldPrimary
)
