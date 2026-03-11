package com.simats.sympcareai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun DoctorLoginScreen(
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: (String, String) -> Unit
) {
    var doctorId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val preferenceManager = remember { com.simats.sympcareai.utils.PreferenceManager(context) }

    // Load saved credentials
    LaunchedEffect(Unit) {
        val (savedId, savedPass, isRemembered) = preferenceManager.getDoctorCredentials()
        if (isRemembered) {
            doctorId = savedId
            password = savedPass
            rememberMe = true
        }
    }

    // Purple Gradient Background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6A5ACD), // SlateBlue
            Color(0xFF9683EC)  // Light Purple
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .systemBarsPadding() // Avoid status bar/camera overlap
            .imePadding(), // Adjust for keyboard
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // content starts from top
        ) {
            Spacer(modifier = Modifier.height(120.dp)) // Push content down below tabs

            // Logo
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape,
                color = White
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.splash_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sign in to continue your health journey",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Color(0xFF424242) // Dark Gray
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Doctor ID Input
            OutlinedTextField(
                value = doctorId,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() }) {
                        doctorId = it 
                        errorMessage = null
                    }
                },
                label = { Text("Doctor ID") },
                placeholder = { Text("Enter your ID (Numbers only)") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6A5ACD)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it 
                    errorMessage = null
                },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
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

            // Error Message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.White, // White on red bg is visible
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Remember Me and Forgot Password Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = White,
                            uncheckedColor = White.copy(alpha = 0.6f),
                            checkmarkColor = Color(0xFF6A5ACD)
                        )
                    )
                    Text(
                        text = "Remember Me",
                        color = Color(0xFF3E2723),
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    )
                }

                TextButton(onClick = onForgotPasswordClick) {
                    Text("Forgot password?", color = Color(0xFF3E2723)) // Dark text
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val scope = rememberCoroutineScope()
            val context = androidx.compose.ui.platform.LocalContext.current
            var isLoading by remember { mutableStateOf(false) }

            // Sign In Button
            Button(
                onClick = { 
                    if (doctorId.isEmpty()) {
                        errorMessage = "Please enter Doctor ID"
                    } else if (password.isEmpty()) {
                        errorMessage = "Please enter Password"
                    } else {
                        errorMessage = ""
                        isLoading = true
                        
                        scope.launch {
                            try {
                                val request = com.simats.sympcareai.data.request.DoctorLoginRequest(
                                    docId = doctorId,
                                    password = password
                                )
                                val apiResponse = com.simats.sympcareai.network.RetrofitClient.apiService.loginDoctor(request)
                                
                                isLoading = false
                                if (apiResponse.isSuccessful) {
                                    val response = apiResponse.body()
                                    if (response?.status == "login_success") {
                                        // Save credentials if Remember Me is checked
                                        preferenceManager.saveDoctorCredentials(doctorId, password, rememberMe)
                                        
                                        android.widget.Toast.makeText(context, "Login Successful", android.widget.Toast.LENGTH_SHORT).show()
                                        val did = response.doctor ?: doctorId
                                        val name = response.fullName ?: "Doctor"
                                        onLoginSuccess(did, name)
                                    } else {
                                        errorMessage = response?.error ?: "Login failed"
                                    }
                                } else {
                                    val errorBody = apiResponse.errorBody()?.string()
                                    try {
                                        val errorJson = org.json.JSONObject(errorBody ?: "{}")
                                        errorMessage = errorJson.optString("error", "Login failed (Server error)")
                                    } catch (e: Exception) {
                                        errorMessage = "Login failed: ${apiResponse.message()}"
                                    }
                                }
                            } catch (e: retrofit2.HttpException) {
                                isLoading = false
                                val errorBody = e.response()?.errorBody()?.string()
                                try {
                                    val errorJson = org.json.JSONObject(errorBody ?: "{}")
                                    errorMessage = errorJson.optString("error", "Login failed")
                                } catch (jsonException: Exception) {
                                    errorMessage = "Login failed: ${e.code()}"
                                }
                                android.util.Log.e("DoctorLogin", "HTTP Error: ${e.code()} - $errorBody")
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "Error: ${e.message}"
                                android.util.Log.e("DoctorLogin", "Login failed", e)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A5ACD) // SlateBlue
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", color = Color(0xFF424242))
                Text(
                    text = "Sign Up",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Terms
            Text(
                text = "By signing in, you agree to our Terms of Service and Privacy Policy",
                color = Color.Black.copy(alpha = 0.4f), // Faint text
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
