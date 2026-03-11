package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.launch
import retrofit2.Response
import com.simats.sympcareai.ui.OtpVerificationDialog

@Composable
fun DoctorSignUpScreen(
    onSignInClick: () -> Unit,
    onSignUpSuccess: (String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var generatedDoctorId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Dialog State
    var showOtpDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Purple Gradient Background (SlateBlue to Light Purple)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6A5ACD), // SlateBlue
            Color(0xFF9683EC)  // Light Purple
        )
    )

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Coroutine Scope
    val scope = rememberCoroutineScope()

    if (showOtpDialog) {
        OtpVerificationDialog(
            onDismiss = { showOtpDialog = false },
            onVerify = { otp ->
                scope.launch {
                    try {
                        val request = com.simats.sympcareai.data.request.VerifyOtpRequest(
                            email = email,
                            otp = otp
                        )
                        val response = com.simats.sympcareai.network.RetrofitClient.apiService.verifyDoctorOtp(request)
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body?.status == "otp_verified") {
                                generatedDoctorId = body.doctor ?: ""
                                showOtpDialog = false
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, body?.error ?: "Verification failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            primaryColor = Color(0xFF6A5ACD)
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissal to force user to login */ },
            title = {
                Text(
                    text = "Account Created Successfully!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your Unique Doctor ID is:",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = generatedDoctorId,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A5ACD)
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(generatedDoctorId))
                            Toast.makeText(context, "Doctor ID copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy ID",
                                tint = Color(0xFF6A5ACD)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Please keep this ID safe for future reference.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSignUpSuccess(generatedDoctorId, fullName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Scrollable content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Account",
                color = Color(0xFF37474F), // Dark Text
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Join SympCareAI today",
                color = Color(0xFF546E7A), // Slightly lighter text
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Full Name
            SignUpTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                placeholder = "Enter your full name",
                icon = Icons.Default.Person,
                iconColor = Color(0xFF6A5ACD) // SlateBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Email ID
            SignUpTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email ID",
                placeholder = "Enter your Email ID",
                icon = Icons.Default.Email,
                iconColor = Color(0xFF6A5ACD)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            SignUpTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                placeholder = "Enter your phone number",
                icon = Icons.Default.Phone,
                iconColor = Color(0xFF6A5ACD),
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Create a password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6A5ACD)) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = null, tint = Color.Gray)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Password Requirements Box
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Password Requirements",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF5E35B1) // Purple theme
                        )
                        com.simats.sympcareai.ui.RequirementItem(
                            text = "At least 8 characters",
                            isMet = com.simats.sympcareai.utils.ValidationUtils.hasMinLength(password)
                        )
                        com.simats.sympcareai.ui.RequirementItem(
                            text = "One uppercase letter",
                            isMet = com.simats.sympcareai.utils.ValidationUtils.hasUpperCase(password)
                        )
                        com.simats.sympcareai.ui.RequirementItem(
                            text = "One number",
                            isMet = com.simats.sympcareai.utils.ValidationUtils.hasDigit(password)
                        )
                        com.simats.sympcareai.ui.RequirementItem(
                            text = "One special character",
                            isMet = com.simats.sympcareai.utils.ValidationUtils.hasSpecialChar(password)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            Button(
                onClick = { 
                    if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phoneNumber.isNotEmpty()) {
                        if (!com.simats.sympcareai.utils.ValidationUtils.isValidGmail(email)) {
                            Toast.makeText(context, com.simats.sympcareai.utils.ValidationUtils.getEmailErrorMessage(), Toast.LENGTH_LONG).show()
                        } else if (!com.simats.sympcareai.utils.ValidationUtils.isValidPassword(password)) {
                            Toast.makeText(context, com.simats.sympcareai.utils.ValidationUtils.getPasswordErrorMessage(), Toast.LENGTH_LONG).show()
                        } else {
                            scope.launch {
                                try {
                                    val request = com.simats.sympcareai.data.request.DoctorRegisterRequest(
                                        fullName = fullName,
                                        phone = phoneNumber,
                                        email = email,
                                        password = password
                                    )
                                    val response = com.simats.sympcareai.network.RetrofitClient.apiService.registerDoctor(request)
                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        if (body?.status == "otp_sent") {
                                            showOtpDialog = true
                                        } else {
                                            Toast.makeText(context, body?.error ?: "Registration failed", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp) // Taller button
                    .shadow(4.dp, RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A5ACD) // SlateBlue
                ),
                shape = RoundedCornerShape(25.dp) // Rounded corners
            ) {
                Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = Color(0xFF424242))
                Text(
                    text = "Sign In",
                    color = Color(0xFF6A5ACD), // SlateBlue
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Text(
                text = "By creating an account, you agree to our Terms of Service and Privacy Policy",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    iconColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = if (icon != null) {
            { Icon(icon, contentDescription = null, tint = iconColor) }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
