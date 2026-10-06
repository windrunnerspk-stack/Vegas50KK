package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.data.local.UserAccountEntity
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasCrimson
import com.example.ui.theme.VegasEmerald
import com.example.ui.theme.VegasGoldLight
import com.example.ui.theme.VegasGoldMetallic
import com.example.ui.theme.VegasGoldPrimary
import com.example.ui.theme.VegasSurface
import com.example.ui.theme.VegasSurfaceCard
import com.example.ui.theme.VegasSurfaceElevated
import com.example.ui.theme.VegasTextMuted
import com.example.ui.theme.VegasTextPrimary
import com.example.ui.theme.VegasTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AuthAndProfileDialog(
    activeUser: UserAccountEntity?,
    currentCurrency: AppCurrency,
    onDismiss: () -> Unit,
    onOpenCurrencyDialog: () -> Unit,
    onSignInWithGoogle: (activityContext: Context, onFinished: (Boolean, String?) -> Unit) -> Unit,
    onLogoutUser: () -> Unit,
    onOpenEmergencyReset: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isSubmitting by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegasSurface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, VegasBorder, RoundedCornerShape(20.dp)),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C220B))
                            .border(1.dp, VegasGoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activeUser != null) Icons.Default.VerifiedUser else Icons.Default.Person,
                            contentDescription = null,
                            tint = VegasGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (activeUser != null) "MI CUENTA VIP" else "AUTENTICACIÓN FIREBASE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (activeUser != null) "Cloud Firestore Sincronizado" else "Sincroniza tus apuestas en la nube",
                            fontSize = 11.sp,
                            color = VegasGoldLight
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = VegasTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (activeUser != null) {
                    // --- ACTIVE USER PROFILE VIEW ---
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF141C2B),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2B200B))
                                        .border(1.5.dp, VegasGoldPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val initials = activeUser.displayName.split(" ")
                                        .mapNotNull { it.firstOrNull() }
                                        .take(2)
                                        .joinToString("")
                                        .uppercase()
                                    Text(
                                        text = initials.ifBlank { "VIP" },
                                        fontWeight = FontWeight.Black,
                                        color = VegasGoldLight,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = activeUser.displayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VegasTextPrimary
                                    )
                                    Text(
                                        text = activeUser.email,
                                        fontSize = 12.sp,
                                        color = VegasTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Member Tier & Cloud Tag
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFF2E220B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, VegasGoldPrimary)
                                ) {
                                    Text(
                                        text = activeUser.vipTier,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VegasGoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = Color(0xFF0F2618),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, VegasEmerald)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = VegasEmerald,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Firestore Activo",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VegasEmerald
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Currency Preference Tile
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onDismiss()
                                onOpenCurrencyDialog()
                            },
                        color = Color(0xFF141C2B),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = VegasGoldLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Moneda del Bankroll", fontSize = 12.sp, color = VegasTextSecondary)
                                    Text(
                                        text = "${currentCurrency.flag} ${currentCurrency.displayName} (${currentCurrency.code})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VegasTextPrimary
                                    )
                                }
                            }
                            Text(text = "Cambiar", fontSize = 11.sp, color = VegasGoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Logout Button
                    OutlinedButton(
                        onClick = onLogoutUser,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasCrimson.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VegasCrimson)
                    ) {
                        Text("Cerrar Sesión", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (onOpenEmergencyReset != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenEmergencyReset()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF381014),
                                contentColor = Color(0xFFFF6B6B)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VegasCrimson.copy(alpha = 0.6f))
                        ) {
                            Text("Reinicio de Emergencia (Borrar Todo)", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }

                } else {
                    // --- UNAUTHENTICATED VIEW: SIGN IN WITH GOOGLE ---
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF141C2B),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Sincroniza con tu Cuenta Google",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegasTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Guarda tus apuestas, estadísticas y bankroll en Cloud Firestore con Firebase Authentication.",
                                fontSize = 11.sp,
                                color = VegasTextMuted
                            )
                        }
                    }

                    if (authError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = authError ?: "", fontSize = 12.sp, color = VegasCrimson, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            isSubmitting = true
                            authError = null
                            onSignInWithGoogle(context) { success, err ->
                                isSubmitting = false
                                if (!success) {
                                    authError = err
                                } else {
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_sign_in_google"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VegasGoldPrimary,
                            contentColor = VegasBg
                        ),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = VegasBg, modifier = Modifier.size(20.dp))
                        } else {
                            Text("CONTINUAR CON GOOGLE", fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    if (onOpenEmergencyReset != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onOpenEmergencyReset()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VegasCrimson.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VegasCrimson)
                        ) {
                            Text("Reinicio de Emergencia (Borrar Todo)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
