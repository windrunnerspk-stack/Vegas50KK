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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasCrimsonBright
import com.example.ui.theme.VegasCrimsonLight
import com.example.ui.theme.VegasSurface
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary

@Composable
fun EmergencyResetDialog(
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegasSurface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, VegasCrimson.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B1014)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta",
                        tint = VegasCrimsonBright,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "VENTANA DE EMERGENCIA",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasCrimsonBright,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Reinicio total y borrado de fábrica",
                        fontSize = 11.sp,
                        color = VegasTextMuted
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFF2B0E12),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, VegasCrimson.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "¡ADVERTENCIA! ESTA ACCIÓN ES PERMANENTE E IRREVERSIBLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasCrimsonLight,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Al confirmar este reinicio de emergencia:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VegasTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "• Se borrarán todas las apuestas registradas (deportes y casino).", fontSize = 11.sp, color = VegasTextSecondary)
                    Text(text = "• Se limpiará todo el historial de movimientos y notas.", fontSize = 11.sp, color = VegasTextSecondary)
                    Text(text = "• Se reiniciarán las métricas, KPIs, Win Rate y gráficas a cero.", fontSize = 11.sp, color = VegasTextSecondary)
                    Text(text = "• El bankroll se restablecerá al monto base inicial configurado.", fontSize = 11.sp, color = VegasTextSecondary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "¿Estás completamente seguro de que deseas vaciar y reiniciar la aplicación?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VegasTextPrimary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VegasCrimson,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_emergency_reset_button")
            ) {
                Text(
                    text = "SÍ, BORRAR TODO DEFINITIVAMENTE",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VegasTextSecondary
                )
            ) {
                Text("CANCELAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}
