package com.simats.sympcareai

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.simats.sympcareai.data.response.AccountInfoResponse
import com.simats.sympcareai.data.response.GenericStatusResponse
import com.simats.sympcareai.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    isDoctor: Boolean,
    userId: String // Pass the user ID (Patient ID or Doc ID) from MainActivity
) {
    val context = LocalContext.current
    
    var fullName by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var displayId by remember { mutableStateOf(userId) }
    var isLoading by remember { mutableStateOf(true) }

    val idLabel = if (isDoctor) "Doctor ID" else "Patient ID"
    val primaryColor = if (isDoctor) Color(0xFFE65100) else Color(0xFF009688)

    val scope = rememberCoroutineScope()

    // Fetch account info on launch
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = if (isDoctor) {
                RetrofitClient.apiService.getDoctorAccountInfo(mapOf("doc_id" to userId))
            } else {
                RetrofitClient.apiService.getPatientAccountInfo(mapOf("patient_id" to userId))
            }

            isLoading = false
            if (response.isSuccessful) {
                val info = response.body()
                fullName = info?.fullName ?: "Unknown"
                email = info?.email ?: "Unknown"
                displayId = (if (isDoctor) info?.docId else info?.patientId) ?: userId
            } else {
                Toast.makeText(context, "Failed to fetch account info", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            isLoading = false
            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun authenticate(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Auth Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Identity Verification")
            .setSubtitle("Confirm your identity to delete your account")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun deleteAccount() {
        scope.launch {
            try {
                val response = if (isDoctor) {
                    RetrofitClient.apiService.deleteDoctor(mapOf("doc_id" to userId))
                } else {
                    RetrofitClient.apiService.deletePatient(mapOf("patient_id" to userId))
                }

                if (response.isSuccessful) {
                    Toast.makeText(context, "Account Deleted Successfully", Toast.LENGTH_LONG).show()
                    onNavigateTo(Screen.Login)
                } else {
                    Toast.makeText(context, "Delete failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account Information", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Main Account Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = primaryColor)
                    } else {
                        Text(
                            text = fullName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    // ID Field
                    AccountField(
                        label = idLabel,
                        value = displayId,
                        icon = Icons.Outlined.Person,
                        helperText = "Your unique $idLabel cannot be changed",
                        iconTint = primaryColor
                    )

                    // Email Field
                    AccountField(
                        label = "Email Address",
                        value = email,
                        icon = Icons.Outlined.Email,
                        helperText = "Primary email for notifications and recovery",
                        iconTint = primaryColor
                    )

                    // Password Field
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                           Spacer(modifier = Modifier.width(8.dp))
                           Text("Password", fontSize = 14.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = "••••••••••••",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                unfocusedContainerColor = Color(0xFFFAFAFA),
                                focusedContainerColor = Color(0xFFFAFAFA)
                            )
                        )
                        
                        TextButton(onClick = { 
                            if (isDoctor) {
                                onNavigateTo(Screen.DoctorResetPasswordLoggedIn)
                            } else {
                                onNavigateTo(Screen.PatientResetPasswordLoggedIn)
                            }
                        }) {
                             Text("Reset password", color = primaryColor, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Danger Zone Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE).copy(alpha = 0.3f)), // Light Red Tint
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Danger Zone", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Once you delete your account, there is no going back. All your health data, reports, and chat history will be permanently deleted.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { 
                            authenticate {
                                deleteAccount()
                            } 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F),
                            containerColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete Account", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountField(
    label: String,
    value: String,
    icon: ImageVector,
    helperText: String,
    iconTint: Color = Color(0xFF009688)
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
           Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
           Spacer(modifier = Modifier.width(8.dp))
           Text(label, fontSize = 14.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        
        Text(
            text = helperText,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
    }
}
