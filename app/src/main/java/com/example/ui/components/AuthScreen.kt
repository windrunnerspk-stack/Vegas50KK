package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppCurrency
import com.example.ui.theme.VegasBg
import com.example.ui.theme.VegasBorder
import com.example.ui.theme.VegasBorderSubtle
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    currentCurrency: AppCurrency,
    onLogin: (email: String, pass: String, onFinished: (Boolean, String?) -> Unit) -> Unit,
    onRegister: (email: String, name: String, pass: String, curr: String, onFinished: (Boolean, String?) -> Unit) -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Iniciar Sesión, 1 = Registrarse
    var selectedTab by remember { mutableIntStateOf(0) }

    // Login state
    var loginEmail by remember { mutableStateOf("latouchettdiego@gmail.com") }
    var loginPassword by remember { mutableStateOf("vegas50k") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register state
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regCurrency by remember { mutableStateOf(currentCurrency.code) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VegasBg)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Casino Chip Logo Emblem
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF2C3954), Color(0xFF151D2C), Color(0xFF070B12))
                        )
                    )
                    .border(2.5.dp, VegasGoldMetallic, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, VegasGoldLight.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "50K",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = VegasGoldMetallic,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Brand Titles
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "VEGAS",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = VegasTextPrimary,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "50K",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = VegasGoldMetallic,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Club Exclusivo de Gestión de Bankroll y Apuestas",
                fontSize = 12.sp,
                color = VegasTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Firebase Security Badge
            Surface(
                color = Color(0xFF251B08),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, VegasGoldPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = VegasGoldLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AUTENTICACIÓN SEGURA • FIREBASE AUTH",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = VegasGoldLight,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector: Iniciar Sesión vs Registrarse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1522))
                    .border(1.dp, VegasBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val isLogin = selectedTab == 0
                val tab1Bg by animateColorAsState(if (isLogin) VegasGoldPrimary else Color.Transparent, label = "tab1")
                val tab1Text by animateColorAsState(if (isLogin) VegasBg else VegasTextSecondary, label = "tab1T")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tab1Bg)
                        .clickable {
                            selectedTab = 0
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("auth_tab_login"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 13.sp,
                        fontWeight = if (isLogin) FontWeight.Black else FontWeight.Medium,
                        color = tab1Text
                    )
                }

                val isReg = selectedTab == 1
                val tab2Bg by animateColorAsState(if (isReg) VegasGoldPrimary else Color.Transparent, label = "tab2")
                val tab2Text by animateColorAsState(if (isReg) VegasBg else VegasTextSecondary, label = "tab2T")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tab2Bg)
                        .clickable {
                            selectedTab = 1
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("auth_tab_register"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Registrarse",
                        fontSize = 13.sp,
                        fontWeight = if (isReg) FontWeight.Black else FontWeight.Medium,
                        color = tab2Text
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Card Container for Auth Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VegasSurfaceCard)
                    .border(1.dp, VegasBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    if (selectedTab == 0) {
                        // --- LOGIN FORM ---
                        Text(
                            text = "Bienvenido de nuevo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegasTextPrimary
                        )
                        Text(
                            text = "Ingresa tus credenciales para acceder al bankroll",
                            fontSize = 11.sp,
                            color = VegasTextMuted
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = VegasGoldLight)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_field"),
                            colors = authFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Contraseña") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VegasGoldLight)
                            },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = VegasTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_field"),
                            colors = authFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )

                        AnimatedVisibility(visible = errorMessage != null) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    fontSize = 12.sp,
                                    color = VegasCrimson,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Submit Login Button
                        Button(
                            onClick = {
                                if (loginEmail.isBlank() || !loginEmail.contains("@")) {
                                    errorMessage = "Por favor ingresa un correo electrónico válido"
                                    return@Button
                                }
                                if (loginPassword.isBlank()) {
                                    errorMessage = "Por favor ingresa tu contraseña"
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                onLogin(loginEmail, loginPassword) { success, err ->
                                    isLoading = false
                                    if (!success) errorMessage = err
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VegasGoldPrimary,
                                contentColor = VegasBg
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = VegasBg, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = "INICIAR SESIÓN CON FIREBASE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                    } else {
                        // --- REGISTRATION FORM ---
                        Text(
                            text = "Crear Cuenta VIP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegasTextPrimary
                        )
                        Text(
                            text = "Regístrate con tu correo para guardar tu historial y bankroll",
                            fontSize = 11.sp,
                            color = VegasTextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Name / Alias
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Nombre o Alias de Jugador") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = VegasGoldLight)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_field"),
                            colors = authFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = VegasGoldLight)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_field"),
                            colors = authFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Contraseña (mínimo 6 caracteres)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VegasGoldLight)
                            },
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = VegasTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_password_field"),
                            colors = authFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Preferred Currency Selector in Registration
                        Text(
                            text = "Selecciona la Moneda de tu Bankroll:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegasTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                AppCurrency.USD,
                                AppCurrency.EUR,
                                AppCurrency.MXN,
                                AppCurrency.COP,
                                AppCurrency.ARS,
                                AppCurrency.USDT
                            ).forEach { curr ->
                                val isSelected = regCurrency == curr.code
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { regCurrency = curr.code },
                                    color = if (isSelected) Color(0xFF2C220B) else Color(0xFF141C2B),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) VegasGoldPrimary else VegasBorder
                                    )
                                ) {
                                    Text(
                                        text = "${curr.flag} ${curr.code} (${curr.symbol})",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) VegasGoldLight else VegasTextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = errorMessage != null) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    fontSize = 12.sp,
                                    color = VegasCrimson,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Submit Registration Button
                        Button(
                            onClick = {
                                if (regEmail.isBlank() || !regEmail.contains("@")) {
                                    errorMessage = "Por favor ingresa un correo electrónico válido"
                                    return@Button
                                }
                                if (regPassword.length < 6) {
                                    errorMessage = "La contraseña de Firebase debe tener al menos 6 caracteres"
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                onRegister(regEmail, regName, regPassword, regCurrency) { success, err ->
                                    isLoading = false
                                    if (!success) errorMessage = err
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("register_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VegasGoldPrimary,
                                contentColor = VegasBg
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = VegasBg, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = "CREAR CUENTA VIP CON FIREBASE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guest / Quick Access Button
            OutlinedButton(
                onClick = onContinueAsGuest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("auth_guest_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VegasBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VegasTextSecondary
                )
            ) {
                Text(
                    text = "Acceso Rápido Directo al Dashboard",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VegasGoldPrimary,
    unfocusedBorderColor = VegasBorder,
    focusedLabelColor = VegasGoldLight,
    unfocusedLabelColor = VegasTextSecondary,
    focusedTextColor = VegasTextPrimary,
    unfocusedTextColor = VegasTextPrimary,
    cursorColor = VegasGoldPrimary,
    focusedContainerColor = VegasSurface,
    unfocusedContainerColor = VegasSurface
)
