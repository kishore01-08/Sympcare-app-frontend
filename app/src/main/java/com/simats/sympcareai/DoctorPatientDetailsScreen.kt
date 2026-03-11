package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Description
import com.simats.sympcareai.data.response.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.data.response.*
import com.simats.sympcareai.network.RetrofitClient
import com.simats.sympcareai.utils.DateTimeUtils
import androidx.compose.runtime.*

@Composable
fun DoctorPatientDetailsScreen(
    patientId: String,
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onDataLoaded: (name: String) -> Unit
) {
    var patientOverview by remember { mutableStateOf<PatientOverviewResponse?>(null) }
    var medicalReports by remember { mutableStateOf<List<MedicalReportResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(patientId) {
        isLoading = true
        errorMessage = null
        try {
            // Fetch Patient Overview
            val overviewResponse = RetrofitClient.apiService.getPatientOverview(mapOf("patient_id" to patientId))
            if (overviewResponse.isSuccessful) {
                patientOverview = overviewResponse.body()
            }
            // Fetch All Reports
            val reportsResponse = RetrofitClient.apiService.listReports(patientId)
            var loadedReports: List<MedicalReportResponse> = emptyList()
            if (reportsResponse.isSuccessful) {
                loadedReports = reportsResponse.body() ?: emptyList()
                medicalReports = loadedReports
            }
            
            // Notify parent of loaded data
            val name = patientOverview?.patientName ?: "Patient $patientId"
//            val latestSymptom = loadedReports
//                .filter { it.type == "Symptom Assessment" || it.sessionId != null }
//                .maxByOrNull { it.uploadedAt }
//                ?.symptoms?.joinToString(", ") ?: "Unknown"
//
//            onDataLoaded(name, latestSymptom)
            
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.message ?: "Failed to load data"
        }
    }

    val fileReports = medicalReports.filter { it.type == "File Analysis" || it.fileUrl != null }
    val symptomReports = medicalReports.filter { it.type == "Symptom Assessment" || it.sessionId != null }
    val latestSymptomReport = symptomReports.maxByOrNull { it.uploadedAt }

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6A5ACD))
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: $errorMessage", color = Color.Red, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onBackClick() }) {
                         Text("Go Back")
                    }
                }
            }
        } else {
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
                    .height(130.dp)
            ) {
                // Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = patientOverview?.patientName ?: "Loading...",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ID: $patientId",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Surface(
                            color = Color(0xFFE0F2F1), // Light Teal
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Active",
                                color = Color(0xFF009688), // Teal
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp) // Overlap
            ) {
                // Patient Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00BFA5),
                            modifier = Modifier.size(60.dp)
                        ) {
                            // Avatar logic or placeholder
                            Box(contentAlignment = Alignment.Center) {
                                // Using Emoji/Icon as per design
                                Text("👨", fontSize = 24.sp) 
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = patientOverview?.patientName ?: "Loading...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = if (latestSymptomReport != null) "Last visit: ${DateTimeUtils.formatToKolkataTime(latestSymptomReport.uploadedAt, "MMM dd, yyyy")}" else "No recent visits",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PatientInfoTile(label = "Age", value = patientOverview?.profile?.age?.toString() ?: "--")
                            PatientInfoTile(label = "Gender", value = patientOverview?.profile?.gender ?: "--")
                            PatientInfoTile(label = "Blood", value = patientOverview?.profile?.bloodGroup ?: "--")
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))

                // Personal Health Report Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                         Row(verticalAlignment = Alignment.Top) {
                             Surface(
                                 color = Color(0xFF448AFF), // Blue
                                 shape = RoundedCornerShape(12.dp),
                                 modifier = Modifier.size(50.dp)
                             ) {
                                 Box(contentAlignment = Alignment.Center) {
                                     Icon(Icons.Outlined.Description, contentDescription = null, tint = Color.White)
                                 }
                             }
                             Spacer(modifier = Modifier.width(16.dp))
                             Column {
                                 Text(
                                     text = "Personal Health Report",
                                     fontWeight = FontWeight.Bold,
                                     fontSize = 16.sp,
                                     color = Color.Black
                                 )
                                 Spacer(modifier = Modifier.height(4.dp))
                                  Text(
                                     text = "Medical reports uploaded by the patient in their profile",
                                     color = Color.Gray,
                                     fontSize = 12.sp,
                                     lineHeight = 16.sp
                                 )
                             }
                         }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                 Icon(Icons.Outlined.Description, contentDescription = null, tint = Color(0xFF448AFF), modifier = Modifier.size(16.dp))
                                 Spacer(modifier = Modifier.width(4.dp))
                                 Text("${fileReports.size} files uploaded", color = Color(0xFF448AFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                             }
                             
                             Row(
                                 verticalAlignment = Alignment.CenterVertically, 
                                 modifier = Modifier.clickable { 
                                     onNavigateTo(Screen.HealthReports(patientId = patientId, initialTab = 0, themeColor = Color(0xFF6A5ACD)))
                                 }
                             ) {
                                 Text("View Report", color = Color(0xFF448AFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                 Spacer(modifier = Modifier.width(4.dp))
                                 Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF448AFF), modifier = Modifier.size(16.dp))
                             }
                        }
                    }
                }
                
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
        }
    }
}


@Composable
fun PatientInfoTile(label: String, value: String) {
    Surface(
        color = Color(0xFFFAFAFA),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(100.dp)
            .height(70.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

