package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simats.sympcareai.network.RetrofitClient
import com.simats.sympcareai.data.request.ForgotPasswordVerifyOtpRequest
import com.simats.sympcareai.data.request.ResetPasswordRequest
import com.simats.sympcareai.data.response.GenericStatusResponse
import retrofit2.Response
import android.widget.Toast

@Composable
fun DoctorResetPasswordScreen(
    email: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var otpVerified by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF6A5ACD) // Purple
    val backgroundColor = Color(0xFFF5F5F5)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (showSuccessDialog) {
        LaunchedEffect(Unit) {
            delay(2000)
            showSuccessDialog = false
            onSaveClick()
        }
        
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF43A047))
            },
            title = {
                Text(text = "Reset Successful", color = Color.Black)
            },
            text = {
                Text(text = "Your password has been reset successfully. Redirecting to login...", color = Color.Black)
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        color = primaryColor,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
            ) {
                 Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                    
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = primaryColor, modifier = Modifier.size(30.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Reset Password",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter OTP and set your new password",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Input Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                         Text(
                            text = "Security Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF37474F)
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // OTP Section
                        Text("One-Time Password (OTP)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             OutlinedTextField(
                                value = otp,
                                onValueChange = { 
                                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                        otp = it
                                        otpError = null
                                        otpVerified = false // Reset verification if changed
                                    }
                                },
                                placeholder = { Text("6-digit OTP", color = Color.Gray) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = otpError != null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = if(otpVerified) Color(0xFF43A047) else primaryColor
                                ),
                                trailingIcon = {
                                    if(otpVerified) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF43A047))
                                    }
                                }
                            )

                            Button(
                                onClick = {
                                    if (otp.length == 6) {
                                        isVerifyingOtp = true
                                        val request = ForgotPasswordVerifyOtpRequest(email, otp)
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.apiService.doctorForgotPasswordVerifyOtp(request)
                                            isVerifyingOtp = false
                                            if (response.isSuccessful && response.body()?.status == "otp_verified") {
                                                otpVerified = true
                                                otpError = null
                                            } else {
                                                otpError = response.body()?.status ?: "Invalid OTP"
                                                Toast.makeText(context, "Verification failed: $otpError", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            isVerifyingOtp = false
                                            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    } else {
                                        otpError = "Enter 6 digits"
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                enabled = otp.length == 6 && !otpVerified && !isVerifyingOtp,
                                modifier = Modifier.height(56.dp)
                            ) {
                                if (isVerifyingOtp) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(if (otpVerified) "Verified" else "Verify", fontSize = 12.sp)
                                }
                            }
                        }
                        if (otpError != null) {
                            Text(text = otpError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // New Password
                        Text("New Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { 
                                newPassword = it 
                                newPasswordError = null
                            },
                            placeholder = { Text("Enter new password", color = Color.Gray) }, 
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor) },
                            trailingIcon = {
                                val image = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(image, contentDescription = null, tint = Color.Gray)
                                }
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = newPasswordError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = primaryColor
                            )
                        )
                         if (newPasswordError != null) {
                            Text(text = newPasswordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                        }

                        // Password Requirements Box
                        if (newPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.5f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Password Requirements",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = primaryColor
                                    )
                                    com.simats.sympcareai.ui.RequirementItem(
                                        text = "At least 8 characters",
                                        isMet = com.simats.sympcareai.utils.ValidationUtils.hasMinLength(newPassword)
                                    )
                                    com.simats.sympcareai.ui.RequirementItem(
                                        text = "One uppercase letter",
                                        isMet = com.simats.sympcareai.utils.ValidationUtils.hasUpperCase(newPassword)
                                    )
                                    com.simats.sympcareai.ui.RequirementItem(
                                        text = "One number",
                                        isMet = com.simats.sympcareai.utils.ValidationUtils.hasDigit(newPassword)
                                    )
                                    com.simats.sympcareai.ui.RequirementItem(
                                        text = "One special character",
                                        isMet = com.simats.sympcareai.utils.ValidationUtils.hasSpecialChar(newPassword)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Confirm Password
                        Text("Confirm Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { 
                                confirmPassword = it 
                                confirmPasswordError = null
                            },
                            placeholder = { Text("Confirm new password", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor) },
                            trailingIcon = {
                                val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(image, contentDescription = null, tint = Color.Gray)
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = confirmPasswordError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = primaryColor
                            )
                        )
                        if (confirmPasswordError != null) {
                             Text(text = confirmPasswordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        // Validation
                        var isValid = true
                        
                        if (!otpVerified) {
                            otpError = "Please verify OTP first"
                            isValid = false
                        }
                        
                         if (!com.simats.sympcareai.utils.ValidationUtils.isValidPassword(newPassword)) {
                            newPasswordError = com.simats.sympcareai.utils.ValidationUtils.getPasswordErrorMessage()
                            isValid = false
                        }

                        if (newPassword != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            isValid = false
                        }

                        if (isValid) {
                            isSaving = true
                            val request = ResetPasswordRequest(email, newPassword, confirmPassword, otp) // Pass otp here for one-step reset
                            scope.launch {
                                try {
                                    val response = RetrofitClient.apiService.doctorResetPassword(request)
                                    isSaving = false
                                    if (response.isSuccessful && response.body()?.status == "password_reset_success") {
                                        showSuccessDialog = true
                                    } else {
                                        Toast.makeText(context, "Error: ${response.body()?.status ?: response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    isSaving = false
                                    Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        disabledContainerColor = primaryColor.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save & Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
