package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.utils.DateTimeUtils
import com.simats.sympcareai.ui.TriageComponent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SymptomReportDetailsScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    symptoms: List<String>,
    disease: String,
    date: String,
    severityScore: Float,
    triage: Int? = null
) {
    val triageColor = when (triage) {
        1 -> Color(0xFFD32F2F)
        2 -> Color(0xFFEF6C00)
        3 -> Color(0xFF388E3C)
        else -> Color.Gray
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Report Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFEF6C00))
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.PatientHome,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Disease / Main Diagnosis Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFFEF6C00), modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("AI Diagnosis", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text(
                        text = disease,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 2. Details Group
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Symptoms Section
                DetailSection(title = "Reported Symptoms", icon = Icons.Default.Assignment, color = Color(0xFF009688)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        symptoms.forEach { symptom ->
                            Surface(
                                color = Color(0xFFE0F2F1),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF009688).copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = symptom,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 13.sp,
                                    color = Color(0xFF00796B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Severity Section
                DetailSection(title = "Severity Score", icon = Icons.Default.Warning, color = Color(0xFFD32F2F)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { severityScore },
                            modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFFD32F2F),
                            trackColor = Color(0xFFFFEBEE)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${(severityScore * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                // Triage Level Section
                DetailSection(title = "Triage Level", icon = Icons.Default.Info, color = triageColor) {
                    TriageComponent(triage = triage)
                }

                // Created Time Section
                DetailSection(title = "Assessment Date", icon = Icons.Default.Event, color = Color(0xFF1976D2)) {
                    Text(
                        text = DateTimeUtils.formatToKolkataTime(date, "EEEE, dd MMM yyyy • hh:mm a"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Back Button
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00))
            ) {
                Text("Return to Reports", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
