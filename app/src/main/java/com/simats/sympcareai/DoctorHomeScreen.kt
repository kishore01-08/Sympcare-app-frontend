package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorHomeScreen(
    onPatientPortalClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onProfileClick: () -> Unit,
    onPatientsTodayClick: () -> Unit,
    onCompletedPatientsClick: () -> Unit,
    patients: List<DoctorPatient> = emptyList(),
    doctorName: String = "Dr. Sarah Johnson",
    patientsTodayCount: Int = 24, 
    completedPatientsCount: Int = 18 
) {
    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.DoctorHome,
                onNavigateTo = onNavigateTo,
                isDoctor = true
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .clickable { onProfileClick() },
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color(0xFF6A5ACD),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WELCOME BACK",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (doctorName.startsWith("Dr.")) doctorName else "Dr. $doctorName",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Status Indicator
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF4CAF50), CircleShape) // Green dot
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Available",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Patients Today Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clickable { onPatientsTodayClick() },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFF1C338), Color(0xFFFF9800))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Column {
                                    Text(
                                        text = "$patientsTodayCount",
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Pending",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Completed Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clickable { onCompletedPatientsClick() },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF00BFA5), Color(0xFF00897B))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                             Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Column {
                                    Text(
                                        text = "$completedPatientsCount",
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Completed",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions Title
                Text(
                    text = "QUICK ACTIONS",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Patient Portal Action
                QuickActionCard(
                    icon = Icons.Default.Person,
                    label = "Patient Portal",
                    iconColor = Color(0xFF6A5ACD), // SlateBlue
                    onClick = onPatientPortalClick,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundGradient = listOf(Color(0xFF6A5ACD), Color(0xFF483D8B))
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Active Patients Title
                Text(
                     text = "ACTIVE PATIENTS",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Active Patients List
                patients.take(5).forEach { patient ->
                    ActivePatientCard(
                        name = patient.name,
                        //symptom = patient.symptom,
                        time = patient.time,
                        status = patient.status,
                        statusColor = patient.statusColor,
                        statusTextColor = patient.statusTextColor,
                        initial = patient.initial,
                        initialBg = patient.initialBg,
                        showCheckbox = false, // Hidden as per request
                        onClick = { onNavigateTo(Screen.DoctorPatientDetails(patient.id)) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                if (patients.isEmpty()) {
                    Text(
                        text = "No patients viewed yet.",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


