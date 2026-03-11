package com.simats.sympcareai

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
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
import com.simats.sympcareai.data.request.ChangePasswordRequest
import com.simats.sympcareai.data.response.GenericStatusResponse
import retrofit2.Response

@Composable
fun ResetPasswordScreen(
    userId: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    isDoctor: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val primaryColor = if (isDoctor) Color(0xFFE65100) else Color(0xFF009688)
    
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    
    // Validation
    val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword
    val isPasswordComplex = com.simats.sympcareai.utils.ValidationUtils.isValidPassword(newPassword)
    
    val isPasswordValid = isPasswordComplex && passwordsMatch && currentPassword.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(primaryColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Key Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Reset Password",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .padding(16.dp)

            ) {
                // Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Enter Password Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                        
                        // Current Password
                        PasswordInput(
                            label = "Current Password",
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            isVisible = isCurrentPasswordVisible,
                            onVisibilityChange = { isCurrentPasswordVisible = !isCurrentPasswordVisible },
                            placeholder = "Enter current password",
                            primaryColor = primaryColor
                        )
                        
                        // New Password
                        PasswordInput(
                            label = "New Password",
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            isVisible = isNewPasswordVisible,
                            onVisibilityChange = { isNewPasswordVisible = !isNewPasswordVisible },
                            placeholder = "Enter new password",
                            primaryColor = primaryColor
                        )
                         if (newPassword.isNotEmpty() && !passwordsMatch && confirmPassword.isNotEmpty()) {
                             ErrorText("Passwords do not match")
                         }

                        // Confirm Password
                        PasswordInput(
                            label = "Confirm New Password",
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            isVisible = isConfirmPasswordVisible,
                            onVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                            placeholder = "Re-enter new password",
                            primaryColor = primaryColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Requirements Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Error,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Password Requirements",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0),
                                fontSize = 14.sp
                            )
                        }
                        com.simats.sympcareai.ui.RequirementItem(text = "At least 8 characters long", isMet = newPassword.length >= 8)
                        com.simats.sympcareai.ui.RequirementItem(text = "Include at least one number", isMet = newPassword.any { it.isDigit() })
                        com.simats.sympcareai.ui.RequirementItem(text = "One uppercase letter", isMet = newPassword.any { it.isUpperCase() })
                        com.simats.sympcareai.ui.RequirementItem(text = "One special character", isMet = newPassword.any { it.isLetterOrDigit().not() && !it.isWhitespace() })
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Actions
                Button(
                    onClick = {
                        isLoading = true
                        val request = ChangePasswordRequest(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword,
                            patientId = if (!isDoctor) userId else null,
                            docId = if (isDoctor) userId else null
                        )
                        
                        scope.launch {
                            try {
                                val response = if (isDoctor) {
                                    RetrofitClient.apiService.doctorChangePassword(request)
                                } else {
                                    RetrofitClient.apiService.changePassword(request)
                                }

                                isLoading = false
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    if (body?.status == "password_changed_success" || body?.status == "password_updated_successfully") {
                                        Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    } else {
                                        Toast.makeText(context, body?.status ?: "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isPasswordValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        disabledContainerColor = primaryColor.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Update Password", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray,
                        containerColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
                
                 Spacer(modifier = Modifier.height(24.dp))
                 
                 Text(
                    text = "For security, you may be logged out after changing your password",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PasswordInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit,
    placeholder: String,
    primaryColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryColor)
            },
            trailingIcon = {
                IconButton(onClick = onVisibilityChange) {
                    Icon(
                        if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun ErrorText(text: String) {
     Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Icon(
            Icons.Default.Close, 
            contentDescription = null, 
            tint = Color.Red,
            modifier = Modifier.size(14.dp).background(Color.Red.copy(alpha=0.1f), CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Red
        )
    }
}
