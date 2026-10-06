package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    onRegisterUser: (email: String, name: String, pass: String, curr: String, onFinished: (Boolean, String?) -> Unit) -> Unit,
    onLoginUser: (email: String, pass: String, onFinished: (Boolean, String?) -> Unit) -> Unit,
    onLogoutUser: () -> Unit,
    onOpenEmergencyReset: (() -> Unit)? = null
) {
    // 0 = Register, 1 = Login
    var authTab by remember { mutableIntStateOf(0) }
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regCurrency by remember { mutableStateOf(currentCurrency.code) }

    var logEmail by remember { mutableStateOf("") }
    var logPassword by remember { mutableStateOf("") }

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
                            .background(VegasGoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = VegasGoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (activeUser != null) "PERFIL VIP" else "CUENTA VEGAS 50K",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VegasTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (activeUser != null) "Membresía & Preferencias" else "Registro con correo electrónico",
                            fontSize = 11.sp,
                            color = VegasTextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VegasSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = VegasTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
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
                    // Logged in User Profile Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F1522))
                            .border(1.dp, VegasBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(VegasGoldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val initials = activeUser.displayName.split(" ")
                                        .mapNotNull { it.firstOrNull() }
                                        .take(2)
                                        .joinToString("")
                                        .uppercase()
                                    Text(
                                        text = initials.ifBlank { "VIP" },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VegasBg
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

                            // Member Tier & Date
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

                                val regDate = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(activeUser.createdAt))
                                Text(
                                    text = "Miembro desde: $regDate",
                                    fontSize = 10.sp,
                                    color = VegasTextMuted
                                )
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = currentCurrency.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Moneda Activa", fontSize = 10.sp, color = VegasTextMuted)
                                    Text(
                                        text = "${currentCurrency.displayName} (${currentCurrency.code} ${currentCurrency.symbol})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VegasTextPrimary
                                    )
                                }
                            }

                            Text(
                                text = "Cambiar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegasGoldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout Button
                    OutlinedButton(
                        onClick = onLogoutUser,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VegasCrimson.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VegasCrimson)
                    ) {
                        Text("Cerrar Sesión / Cambiar de Usuario", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                    // Registration / Login Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F1522))
                            .border(1.dp, VegasBorder, RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authTab == 0) VegasGoldPrimary else Color.Transparent)
                                .clickable {
                                    authTab = 0
                                    authError = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Crear Cuenta",
                                fontSize = 12.sp,
                                fontWeight = if (authTab == 0) FontWeight.Black else FontWeight.Medium,
                                color = if (authTab == 0) VegasBg else VegasTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authTab == 1) VegasGoldPrimary else Color.Transparent)
                                .clickable {
                                    authTab = 1
                                    authError = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 12.sp,
                                fontWeight = if (authTab == 1) FontWeight.Black else FontWeight.Medium,
                                color = if (authTab == 1) VegasBg else VegasTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (authTab == 0) {
                        // Registration Form
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Nombre o Alias de Jugador") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = VegasGoldLight) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_reg_name"),
                            colors = authFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = VegasGoldLight) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_reg_email"),
                            colors = authFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Contraseña") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = VegasGoldLight) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_reg_pass"),
                            colors = authFieldColors(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preferred Currency Selector in Registration
                        Text(text = "Moneda Principal:", fontSize = 11.sp, color = VegasTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(AppCurrency.USD, AppCurrency.EUR, AppCurrency.MXN, AppCurrency.COP).forEach { curr ->
                                val isSelected = regCurrency == curr.code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF2C220B) else Color(0xFF141C2B))
                                        .border(1.dp, if (isSelected) VegasGoldPrimary else VegasBorder, RoundedCornerShape(8.dp))
                                        .clickable { regCurrency = curr.code }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${curr.flag} ${curr.code}",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) VegasGoldLight else VegasTextSecondary
                                    )
                                }
                            }
                        }

                        if (authError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = authError ?: "", fontSize = 11.sp, color = VegasCrimson, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (regEmail.isBlank() || !regEmail.contains("@")) {
                                    authError = "Por favor ingresa un correo electrónico válido"
                                    return@Button
                                }
                                if (regPassword.length < 4) {
                                    authError = "La contraseña debe tener al menos 4 caracteres"
                                    return@Button
                                }
                                isSubmitting = true
                                authError = null
                                onRegisterUser(regEmail, regName, regPassword, regCurrency) { success, err ->
                                    isSubmitting = false
                                    if (!success) authError = err
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("auth_submit_register"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VegasGoldPrimary, contentColor = VegasBg)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = VegasBg, modifier = Modifier.size(20.dp))
                            } else {
                                Text("CREAR CUENTA REGISTRADA", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }

                    } else {
                        // Login Form
                        OutlinedTextField(
                            value = logEmail,
                            onValueChange = { logEmail = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = VegasGoldLight) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_login_email"),
                            colors = authFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = logPassword,
                            onValueChange = { logPassword = it },
                            label = { Text("Contraseña") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = VegasGoldLight) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_login_pass"),
                            colors = authFieldColors(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )

                        if (authError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = authError ?: "", fontSize = 11.sp, color = VegasCrimson, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (logEmail.isBlank()) {
                                    authError = "Por favor ingresa tu correo"
                                    return@Button
                                }
                                isSubmitting = true
                                authError = null
                                onLoginUser(logEmail, logPassword) { success, err ->
                                    isSubmitting = false
                                    if (!success) authError = err
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("auth_submit_login"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VegasGoldPrimary, contentColor = VegasBg)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = VegasBg, modifier = Modifier.size(20.dp))
                            } else {
                                Text("INICIAR SESIÓN", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VegasGoldPrimary,
    unfocusedBorderColor = VegasBorder,
    focusedLabelColor = VegasGoldLight,
    unfocusedLabelColor = VegasTextSecondary,
    focusedTextColor = VegasTextPrimary,
    unfocusedTextColor = VegasTextPrimary,
    cursorColor = VegasGoldPrimary
)
