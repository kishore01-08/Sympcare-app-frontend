package com.simats.sympcareai

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.data.response.AIAnalysisResponse
import com.simats.sympcareai.ai_engine.AIEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SymptomAnalysisResultScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    analysisResult: AIAnalysisResponse,
    selectedSymptoms: List<String>
) {
    BackHandler { onBackClick() }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Health Analysis", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            AnalysisResultView(analysisResult)

            // Selected Symptoms Section
            SectionCard(title = "Reported Symptoms", color = Color(0xFF009688)) {
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    selectedSymptoms.forEach { symptom ->
                        Surface(
                            color = Color(0xFFE0F2F1),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = symptom,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 13.sp,
                                color = Color(0xFF009688),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Possible Diseases / Differential Analysis
            if (!analysisResult.possibleDiseases.isNullOrEmpty()) {
                SectionCard(title = "Possible Conditions", color = Color(0xFF3F51B5)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        analysisResult.possibleDiseases.forEach { disease ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF3F51B5), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(disease.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                                Surface(
                                    color = Color(0xFFE8EAF6),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = AIEngine.formatProbability(disease.probability),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3F51B5)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Health Tips / Recommendations
            SectionCard(title = "Health Recommendations", color = Color(0xFF4CAF50)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RecommendationItem("Monitor your symptoms closely over the next 24-48 hours.")
                    RecommendationItem("Stay hydrated and maintain a balanced diet.")
                    RecommendationItem("Ensure adequate rest to support your immune system.")
                    
                    if ((analysisResult.triage ?: 3) <= 1) {
                        RecommendationItem("Seek immediate medical attention as your symptoms indicate high severity.")
                    } else if ((analysisResult.triage ?: 3) == 2) {
                        RecommendationItem("Schedule a consultation with a healthcare professional soon.")
                    } else {
                        RecommendationItem("Consider a routine check-up if symptoms persist or worsen.")
                    }
                }
            }

            // Disclaimer
            Surface(
                color = Color(0xFFFFF9C4),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFBC02D), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "This AI analysis is for informational purposes only and is not a substitute for professional medical advice, diagnosis, or treatment.",
                        fontSize = 12.sp,
                        color = Color(0xFF616161),
                        lineHeight = 16.sp
                    )
                }
            }
            
            Button(
                onClick = { onNavigateTo(Screen.PatientHome) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
            ) {
                Text("Back to Home", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
