package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnaCareLogo
import com.example.ui.components.BiometricDialog
import com.example.ui.components.MfaVerificationDialog
import com.example.ui.theme.HealthNormalGreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SkyLight
import com.example.ui.theme.TealAccent
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.PortalViewModel

@Composable
fun AuthScreen(
    viewModel: PortalViewModel,
    authState: AuthState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val showMfaDialog by viewModel.showMfaDialog.collectAsState()
    val showBiometricDialog by viewModel.showBiometricDialog.collectAsState()
    val currentMfaCode by viewModel.currentMfaCode.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NavyDark,
                        NavyPrimary,
                        Color(0xFF0F2544)
                    )
                )
            )
            .statusBarsPadding()
            .imePadding()
            .testTag("auth_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .testTag("auth_header_logo")
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .height(90.dp)
                            .width(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ana_care_app_logo_1787863935033),
                            contentDescription = "ANA CARE Application Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Auth Card based on AuthState
                when (authState) {
                    is AuthState.Login -> LoginCard(
                        viewModel = viewModel,
                        onNavigateToSignUp = { viewModel.setAuthState(AuthState.SignUp) }
                    )
                    is AuthState.SignUp -> SignUpCard(
                        viewModel = viewModel,
                        onNavigateToLogin = { viewModel.setAuthState(AuthState.Login) }
                    )
                    is AuthState.EmailVerification -> EmailVerificationCard(
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.setAuthState(AuthState.SignUp) }
                    )
                    is AuthState.ProfileSetup -> ProfileSetupCard(
                        viewModel = viewModel
                    )
                    else -> LoginCard(
                        viewModel = viewModel,
                        onNavigateToSignUp = { viewModel.setAuthState(AuthState.SignUp) }
                    )
                }
            }
        }
    }

    // Modal Dialogs for Authentication Screen
    if (showBiometricDialog) {
        BiometricDialog(
            userName = activeAccount?.name ?: "User",
            onSuccess = { viewModel.completeBiometricLogin() },
            onDismiss = { viewModel.setShowBiometricDialog(false) }
        )
    }

    if (showMfaDialog) {
        MfaVerificationDialog(
            generatedCode = currentMfaCode,
            onVerify = { viewModel.verifyMfaAndCompleteLogin(it) },
            onDismiss = { viewModel.setShowMfaDialog(false) }
        )
    }
}

@Composable
private fun LoginCard(
    viewModel: PortalViewModel,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("eleanor.vance@example.com") }
    var password by remember { mutableStateOf("••••••••") }
    var showPassword by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sign in to access your health portal",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE0F2FE),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Login",
                            tint = TealAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Role Demo Shortcuts
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT ROLE / DEMO ACCOUNT",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "1-Tap to choose",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isPatientSelected = email == "eleanor.vance@example.com"
                        val isCaregiverSelected = email == "james.vance@example.com"
                        val isDoctorSelected = email == "dr.jenkins@anacare.org"
                        val isAdminSelected = email == "admin@anacare.org"

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPatientSelected) NavyPrimary else Color(0xFFE0F2FE),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    email = "eleanor.vance@example.com"
                                    password = "••••••••"
                                }
                                .testTag("quick_select_patient")
                        ) {
                            Text(
                                text = "👤 Patient",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPatientSelected) Color.White else NavyPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCaregiverSelected) Color(0xFF6D28D9) else Color(0xFFEDE9FE),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    email = "james.vance@example.com"
                                    password = "••••••••"
                                }
                                .testTag("quick_select_caregiver")
                        ) {
                            Text(
                                text = "🤝 Caregiver",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCaregiverSelected) Color.White else Color(0xFF6D28D9),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDoctorSelected) Color(0xFF047857) else Color(0xFFD1FAE5),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    email = "dr.jenkins@anacare.org"
                                    password = "••••••••"
                                }
                                .testTag("quick_select_doctor")
                        ) {
                            Text(
                                text = "🩺 Doctor",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDoctorSelected) Color.White else Color(0xFF047857),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAdminSelected) Color(0xFFB45309) else Color(0xFFFEF3C7),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    email = "admin@anacare.org"
                                    password = "••••••••"
                                }
                                .testTag("quick_select_admin")
                        ) {
                            Text(
                                text = "🛡️ Admin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAdminSelected) Color.White else Color(0xFFB45309),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email", tint = TealAccent)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Password", tint = TealAccent)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.loginWithCredentials(email, password) {}
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1-Tap Quick Enter Portal Button
            val activeRoleTitle = when (email) {
                "dr.jenkins@anacare.org" -> "Doctor Workstation"
                "james.vance@example.com" -> "Caregiver Portal"
                "admin@anacare.org" -> "Admin Dashboard"
                else -> "Patient Dashboard"
            }

            Button(
                onClick = {
                    viewModel.loginWithCredentials(email, password, bypassMfa = true) {}
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (email) {
                        "dr.jenkins@anacare.org" -> Color(0xFF047857)
                        "james.vance@example.com" -> Color(0xFF6D28D9)
                        "admin@anacare.org" -> Color(0xFFB45309)
                        else -> NavyPrimary
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("quick_direct_login_button")
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("1-Tap Enter: $activeRoleTitle", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Login Button (with Multi-Factor Authentication Trigger)
            OutlinedButton(
                onClick = {
                    viewModel.loginWithCredentials(email, password, bypassMfa = false) {}
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("login_submit_button")
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In with 2FA Code", color = NavyPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Biometric Quick Login Button
            OutlinedButton(
                onClick = { viewModel.setShowBiometricDialog(true) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("biometric_login_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Icon",
                    tint = TealAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Biometric Quick Unlock", color = NavyPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Switch to Signup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign Up",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent,
                    modifier = Modifier
                        .clickable(onClick = onNavigateToSignUp)
                        .testTag("navigate_signup_button")
                )
            }
        }
    }
}

@Composable
private fun SignUpCard(
    viewModel: PortalViewModel,
    onNavigateToLogin: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("PATIENT") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("signup_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Text(
                text = "Create Portal Account",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Step 1 of 3: Role & Credentials",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Selector Tab
            Text(
                text = "Select Account Profile Type",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Patient Role
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedRole == "PATIENT") Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (selectedRole == "PATIENT") TealAccent else Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedRole = "PATIENT" }
                        .testTag("role_patient_select")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👤", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Patient / Family",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == "PATIENT") NavyPrimary else Color(0xFF475569)
                        )
                    }
                }

                // Medical Professional Role
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedRole == "MEDICAL_PROFESSIONAL") Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (selectedRole == "MEDICAL_PROFESSIONAL") TealAccent else Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedRole = "MEDICAL_PROFESSIONAL" }
                        .testTag("role_doctor_select")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🩺", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Medical Pro",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == "MEDICAL_PROFESSIONAL") NavyPrimary else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorText = ""
                },
                label = { Text("Email Address *") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TealAccent) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorText = ""
                },
                label = { Text("Create Password *") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealAccent) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorText = ""
                },
                label = { Text("Confirm Password *") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealAccent) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_confirm_password_input")
            )

            if (errorText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorText, color = Color(0xFFEF4444), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorText = "Please fill in all fields."
                    } else if (password != confirmPassword) {
                        errorText = "Passwords do not match."
                    } else {
                        viewModel.startSignupFlow(selectedRole, email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("signup_next_button")
            ) {
                Text("Proceed to Email Verification", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign In",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }
        }
    }
}

@Composable
private fun EmailVerificationCard(
    viewModel: PortalViewModel,
    onNavigateBack: () -> Unit
) {
    val dispatchedCode = viewModel.signupVerificationCode.value
    var inputCode by remember { mutableStateOf(dispatchedCode) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("email_verification_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE0F2FE),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Verified",
                        tint = TealAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Verify Your Email",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Step 2 of 3: Enter the 6-digit confirmation code sent to ${viewModel.signupEmail.value}",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Verification Code Helper Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("VERIFICATION CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        Text(
                            text = dispatchedCode,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyPrimary
                        )
                    }
                    TextButton(onClick = { inputCode = dispatchedCode }) {
                        Text("Auto-fill", fontWeight = FontWeight.Bold, color = TealAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputCode,
                onValueChange = { if (it.length <= 6) inputCode = it },
                label = { Text("6-Digit Verification Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verification_code_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.verifySignupEmail(inputCode) },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("verify_email_button")
            ) {
                Text("Verify & Continue to Profile", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = onNavigateBack) {
                Text("Back to Credentials", color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
private fun ProfileSetupCard(
    viewModel: PortalViewModel
) {
    val isDoctor = viewModel.signupRole.value == "MEDICAL_PROFESSIONAL"

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf(if (isDoctor) "1980-05-12" else "1960-03-20") }
    var gender by remember { mutableStateOf("Female") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var allergies by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var insuranceProvider by remember { mutableStateOf("") }
    var insurancePolicyNo by remember { mutableStateOf("") }

    // Doctor Specific Fields
    var specialty by remember { mutableStateOf(if (isDoctor) "Internal Medicine & Home Care" else "") }
    var licenseNumber by remember { mutableStateOf(if (isDoctor) "LIC-NY-99201" else "") }
    var hospitalClinic by remember { mutableStateOf(if (isDoctor) "ANA Care Medical Group" else "") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_setup_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Text(
                text = if (isDoctor) "Doctor Profile Setup" else "Patient Health Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Step 3 of 3: Configure your encrypted medical profile",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Legal Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealAccent) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TealAccent) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!isDoctor) {
                // Patient Specific Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Type") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Known Allergies (e.g. Penicillin, Latex)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Contact (Name & Phone)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = insuranceProvider,
                    onValueChange = { insuranceProvider = it },
                    label = { Text("Insurance Provider (e.g. Medicare / BCBS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Doctor Fields
                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Medical Specialty *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("State Medical License # *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hospitalClinic,
                    onValueChange = { hospitalClinic = it },
                    label = { Text("Hospital / Practice Affiliation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.completeProfileSetup(
                            name = name,
                            phone = phone,
                            dob = dob,
                            gender = gender,
                            bloodGroup = bloodGroup,
                            allergies = allergies,
                            emergencyContact = emergencyContact,
                            insuranceProvider = insuranceProvider,
                            insurancePolicyNo = insurancePolicyNo,
                            specialty = specialty,
                            licenseNumber = licenseNumber,
                            hospitalClinic = hospitalClinic
                        )
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("complete_profile_button")
            ) {
                Text("Complete Profile & Open Portal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
