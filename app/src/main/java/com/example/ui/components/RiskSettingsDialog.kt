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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurface
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import java.util.Locale

@Composable
fun RiskSettingsDialog(
    initialStartingBankroll: Double,
    initialWeeklyLossLimit: Double,
    initialWeeklyStakeLimit: Double,
    currency: AppCurrency,
    onDismiss: () -> Unit,
    onSaveSettings: (startingBankroll: Double, weeklyLossLimit: Double, weeklyStakeLimit: Double) -> Unit
) {
    var startingBankrollStr by remember {
        mutableStateOf(String.format(Locale.US, "%.0f", initialStartingBankroll))
    }
    var weeklyLossLimitStr by remember {
        mutableStateOf(String.format(Locale.US, "%.0f", initialWeeklyLossLimit))
    }
    var weeklyStakeLimitStr by remember {
        mutableStateOf(String.format(Locale.US, "%.0f", initialWeeklyStakeLimit))
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegasSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, VegasBorder, RoundedCornerShape(20.dp)),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(VegasGoldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = VegasGoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GESTIÓN DE RIESGO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Valores en ${currency.code} (${currency.symbol})",
                        fontSize = 11.sp,
                        color = VegasTextMuted
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Configura los límites para proteger tu capital y evitar la sobreexposición en rachas negativas.",
                    fontSize = 12.sp,
                    color = VegasTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Starting Bankroll
                OutlinedTextField(
                    value = startingBankrollStr,
                    onValueChange = { startingBankrollStr = it },
                    label = { Text("Bankroll Base Inicial (${currency.symbol})") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("starting_bankroll_input"),
                    colors = riskFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Weekly Loss Limit (Stop Loss)
                OutlinedTextField(
                    value = weeklyLossLimitStr,
                    onValueChange = { weeklyLossLimitStr = it },
                    label = { Text("Tope Semanal de Pérdidas (${currency.symbol})") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("loss_limit_input"),
                    colors = riskFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Weekly Stake Limit
                OutlinedTextField(
                    value = weeklyStakeLimitStr,
                    onValueChange = { weeklyStakeLimitStr = it },
                    label = { Text("Límite Semanal de Stake Total (${currency.symbol})") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stake_limit_input"),
                    colors = riskFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bankroll = startingBankrollStr.toDoubleOrNull()
                    val lossLimit = weeklyLossLimitStr.toDoubleOrNull()
                    val stakeLimit = weeklyStakeLimitStr.toDoubleOrNull()

                    if (bankroll == null || bankroll <= 0) {
                        errorMessage = "Ingresa un bankroll inicial válido"
                        return@Button
                    }
                    if (lossLimit == null || lossLimit <= 0) {
                        errorMessage = "Ingresa un tope de pérdidas válido"
                        return@Button
                    }
                    if (stakeLimit == null || stakeLimit <= 0) {
                        errorMessage = "Ingresa un límite de stake válido"
                        return@Button
                    }

                    onSaveSettings(bankroll, lossLimit, stakeLimit)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VegasGoldPrimary,
                    contentColor = VegasBg
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_risk_settings_button")
            ) {
                Text("Guardar Parámetros", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VegasTextSecondary)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun riskFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VegasGoldPrimary,
    unfocusedBorderColor = VegasBorder,
    focusedLabelColor = VegasGoldLight,
    unfocusedLabelColor = VegasTextSecondary,
    focusedTextColor = VegasTextPrimary,
    unfocusedTextColor = VegasTextPrimary,
    cursorColor = VegasGoldPrimary
)
