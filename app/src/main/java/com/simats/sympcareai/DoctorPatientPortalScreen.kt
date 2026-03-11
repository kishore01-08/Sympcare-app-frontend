package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPatientPortalScreen(
    onBackClick: () -> Unit,
    onViewPatientDetails: (String) -> Unit, // Takes Patient ID
) {
    var patientId by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ... (Header and Secure Access Card remain same - truncated for brevity in replacement if possible, but I'll keeping context)
            // Actually, I can target the specific block for Text Field and Button
            
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                            )
                        )
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6A5ACD))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Patient Portal",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter Patient ID to access patient records",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Secure Access Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Light Blue
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF90CAF9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF1976D2))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Secure Access:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Patient data is encrypted and protected by HIPAA compliance standards.",
                                color = Color(0xFF1976D2),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Patient ID Input
                Text(
                    text = "Patient ID",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = patientId,
                    onValueChange = { 
                        if (it.length <= 9 && it.all { char -> char.isDigit() }) {
                            patientId = it
                            isError = false
                        }
                    },
                    placeholder = { Text("123456789", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("ID must be exactly 9 digits", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFFE0E0E0),
                        unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Buttons
                Button(
                    onClick = { 
                        if (patientId.length == 9) {
                            onViewPatientDetails(patientId)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(28.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)), // Teal
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Patient Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Create New Patient Button

                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
