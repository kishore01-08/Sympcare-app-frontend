package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
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
fun CreatePatientScreen(
    onBackClick: () -> Unit,
    onSubmitAndAnalyze: (String, String) -> Unit // Takes ID and Symptoms
) {
    var patientId by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
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
                                colors = listOf(Color(0xFFFF6E6E), Color(0xFFFF9F43))
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
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFF6E6E))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "New Patient",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create new Patient ID to access patient portal",
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
                // Create Patient ID Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             Box(
                                 modifier = Modifier
                                     .size(4.dp, 20.dp)
                                     .background(Color(0xFFFF9F43), RoundedCornerShape(2.dp))
                             )
                             Spacer(modifier = Modifier.width(8.dp))
                             Text(
                                 text = "Create Patient ID",
                                 fontWeight = FontWeight.Bold,
                                 fontSize = 16.sp
                             )
                         }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = patientId,
                            onValueChange = { patientId = it },
                            placeholder = { Text("Enter new patient ID", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFFAFAFA),
                                unfocusedContainerColor = Color(0xFFFAFAFA),
                                focusedBorderColor = Color(0xFFE0E0E0),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Symptoms Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             Box(
                                 modifier = Modifier
                                     .size(4.dp, 20.dp)
                                     .background(Color(0xFF7B61FF), RoundedCornerShape(2.dp))
                             )
                             Spacer(modifier = Modifier.width(8.dp))
                             Text(
                                 text = "Symptoms",
                                 fontWeight = FontWeight.Bold,
                                 fontSize = 16.sp
                             )
                         }
                        Spacer(modifier = Modifier.height(16.dp))
                         OutlinedTextField(
                            value = symptoms,
                            onValueChange = { symptoms = it },
                            placeholder = { 
                                Text(
                                    "Type patient symptoms separated by commas (e.g., Headache, Fever, Fatigue)", 
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFFAFAFA),
                                unfocusedContainerColor = Color(0xFFFAFAFA),
                                focusedBorderColor = Color(0xFFE0E0E0),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF7B61FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enter symptoms separated by commas.\nExample: Headache, Dizziness, Nausea",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Symptoms Preview Tag
                        if (symptoms.isNotEmpty()) {
                             Surface(
                                color = Color(0xFFEDE7F6), // Light Purple
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "3 symptoms entered: Headache, Dizziness, Fatigue", // Mocked for design match, logic can be dynamic
                                    color = Color(0xFF673AB7),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { onSubmitAndAnalyze(patientId, symptoms) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(28.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61FF)), // Purple
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Submit & Analyze", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
