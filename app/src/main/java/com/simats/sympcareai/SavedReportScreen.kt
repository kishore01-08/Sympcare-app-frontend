package com.simats.sympcareai

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.simats.sympcareai.data.response.FileAnalysisResponse
import com.simats.sympcareai.network.RetrofitClient
import io.noties.markwon.Markwon
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SavedReportScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    reportId: Int
) {
    val context = LocalContext.current
    var fileResult by remember { mutableStateOf<FileAnalysisResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val markwon = remember { Markwon.create(context) }

    LaunchedEffect(reportId) {
        if (reportId != -1) {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getReportAnalysis(reportId)
                isLoading = false
                if (response.isSuccessful) {
                    fileResult = response.body()
                } else {
                    errorMessage = "Failed to fetch report details: ${response.code()}"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Network error: ${e.message}"
            }
        } else {
            isLoading = false
            errorMessage = "Invalid report ID."
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Report", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFEF6C00))
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(errorMessage!!, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            }
        } else {
            val result = fileResult!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Info
                Text("AI-generated insights for your report", color = Color(0xFFEF6C00), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

                // Triage & Severity Score Section
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val displayTriage = result.triage ?: 3
                    val displaySeverity = result.severityScore ?: 0.0f

                    // Triage Level
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = when(displayTriage) {
                            1 -> Color(0xFFFFEBEE)
                            2 -> Color(0xFFFFF3E0)
                            else -> Color(0xFFE8F5E9)
                        }),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Triage Level", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = displayTriage.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = when(displayTriage) {
                                    1 -> Color.Red
                                    2 -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                }
                            )
                        }
                    }

                    // Severity Score
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Severity Score", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${(displaySeverity * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }

                // Analysis Based On
                SectionCard(title = "Analysis Based On", color = Color(0xFF009688)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color(0xFF673AB7))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Report Symptoms", fontWeight = FontWeight.SemiBold)
                        }
                        // Dynamic Chips
                        val symptoms = result.symptoms ?: emptyList()
                        if (symptoms.isNotEmpty()) {
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                symptoms.forEach { symptom ->
                                    SuggestionChip(
                                        onClick = {}, 
                                        label = { Text(symptom) }, 
                                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFEDE7F6))
                                    )
                                }
                            }
                        } else {
                             Text("No symptoms recorded", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 32.dp))
                        }
                    }
                }

                // Main Analysis Card
                SectionCard(title = "Detailed AI Analysis", color = Color(0xFFEF6C00)) {
                    val reportText = result.report ?: "No detailed analysis available."
                    AndroidView(
                        factory = { context -> TextView(context).apply { 
                            setTextColor(android.graphics.Color.BLACK)
                            textSize = 15f
                        } },
                        update = { textView ->
                            markwon.setMarkdown(textView, reportText)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Possible Health Conditions
                if (!result.possibleDiseases.isNullOrEmpty()) {
                    SectionCard(title = "Possible Health Conditions", color = Color(0xFFF44336)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            result.possibleDiseases?.forEach { disease ->
                                ConditionItem(
                                    title = disease.name,
                                    risk = if ((disease.probability ?: 0f) > 0.5f) "High Risk" else "Moderate Risk",
                                    color = if ((disease.probability ?: 0f) > 0.5f) Color(0xFFF44336) else Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }

                // AI Recommendations
                SectionCard(title = "AI Recommendations", color = Color(0xFF009688)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                         RecommendationItem("Follow the prescribed consultation guidance.")
                         RecommendationItem("Monitor your symptoms for any changes.")
                         RecommendationItem("Reach out to a healthcare professional if needed.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onBackClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to History")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
