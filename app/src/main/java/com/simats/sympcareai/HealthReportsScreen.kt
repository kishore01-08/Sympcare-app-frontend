package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.data.response.*
import com.simats.sympcareai.network.RetrofitClient
import com.simats.sympcareai.utils.DateTimeUtils

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HealthReportsScreen(
    patientId: String,
    initialTab: Int = 0,
    themeColor: Color = Color(0xFFEF6C00), // Default Orange
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var reports by remember { mutableStateOf<List<MedicalReportResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val pagerState = rememberPagerState(initialPage = initialTab, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty()) {
            isLoading = true
            errorMessage = null
            try {
                // 1. Get Patient Name for filtering history
                val accountInfoResponse = RetrofitClient.apiService.getPatientAccountInfo(mapOf("patient_id" to patientId))
                val fullName = if (accountInfoResponse.isSuccessful) accountInfoResponse.body()?.fullName else null
                
                // 2. Load Medical Reports
                val reportsResponse = RetrofitClient.apiService.listReports(patientId)
                val medicalReportsList = if (reportsResponse.isSuccessful) reportsResponse.body() ?: emptyList() else emptyList()
                
                // 3. Load Chat History
                val chatHistoryResponse = RetrofitClient.apiService.getChatHistory(patientId)
                val chatHistoryList = if (chatHistoryResponse.isSuccessful) {
                    chatHistoryResponse.body()?.history ?: emptyList()
                } else emptyList()
                
                // Filter chat history for this patient and convert to MedicalReportResponse
                val filteredChatReports = if (fullName != null) {
                    chatHistoryList.filter { it.user == fullName }.map { chat ->
                        MedicalReportResponse(
                            id = -100 - chatHistoryList.indexOf(chat), // Artificial ID
                            patientId = patientId,
                            sessionId = null,
                            fileUrl = null,
                            analysis = chat.disease,
                            uploadedAt = chat.date,
                            type = "Symptom Assessment",
                            symptoms = chat.symptoms,
                            triage = chat.triage,
                            severityScore = chat.severityScore
                        )
                    }
                } else emptyList()
                
                reports = (medicalReportsList + filteredChatReports).sortedByDescending { it.uploadedAt }
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error loading data: ${e.message}"
                android.util.Log.e("HealthReports", "Error fetching reports", e)
            }
        } else {
            isLoading = false
            errorMessage = "Please login to view reports."
        }
    }

    // Filter reports
    val medicalReports = reports.filter { it.type == "File Analysis" }
    val symptomReports = reports.filter { it.type != "File Analysis" }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.PatientHome,
                onNavigateTo = onNavigateTo
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeColor) 
                    .padding(top = 24.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                     Box(modifier = Modifier.fillMaxWidth()) {
                         IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                         }
                         Surface(
                             shape = CircleShape,
                             color = Color.White,
                             modifier = Modifier.size(56.dp).align(Alignment.Center),
                             shadowElevation = 4.dp
                         ) {
                             Box(contentAlignment = Alignment.Center) {
                                  Icon(Icons.Default.Description, contentDescription = null, tint = themeColor, modifier = Modifier.size(28.dp))
                             }
                         }
                     }
                     Spacer(modifier = Modifier.height(16.dp))
                     Text("Health Reports", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                 }
            }

            // Tabs
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = themeColor,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Color.White
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Medical Reports", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Symptom Reports", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                val currentReports = if (page == 0) medicalReports else symptomReports
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = themeColor)
                        }
                    } else if (errorMessage != null) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
                        }
                    } else if (currentReports.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 64.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    if (page == 0) Icons.Default.Description else Icons.Default.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    if (page == 0) "No medical reports found" else "No symptom assessments found",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            currentReports.forEach { report ->
                                val isFileAnalysis = report.type == "File Analysis"
                                val reportTags = if (isFileAnalysis) {
                                    listOf("Lab Result", "AI Analysis")
                                } else {
                                    report.symptoms?.take(3) ?: listOf("Symptoms", "AI Insights")
                                }

                                ReportCard(
                                    title = if (isFileAnalysis) "Medical Report Analysis" else "Health Consultation",
                                    date = DateTimeUtils.formatToKolkataTime(report.uploadedAt, "dd MMM yyyy"), 
                                    doctorName = "AI Assistant",
                                    type = report.type ?: "General Analysis",
                                    typeIcon = if (isFileAnalysis) Icons.Default.Description else Icons.Default.Chat,
                                    tags = reportTags,
                                    status = "COMPLETED",
                                    statusColor = Color(0xFF4CAF50),
                                    onClick = { 
                                        if (isFileAnalysis) {
                                            if (report.id != null) {
                                                onNavigateTo(Screen.ReportAnalysis(report.id))
                                            }
                                        } else {
                                            onNavigateTo(Screen.SymptomReportDetails(
                                                symptoms = report.symptoms ?: emptyList(),
                                                disease = report.analysis ?: "Inconclusive",
                                                date = report.uploadedAt ?: "",
                                                severityScore = report.severityScore ?: 0.0f,
                                                triage = report.triage
                                            ))
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportCard(
    title: String,
    date: String,
    doctorName: String,
    type: String,
    typeIcon: ImageVector,
    tags: List<String>,
    status: String,
    statusColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = status,
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Details
            Text(date, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray) 
                Spacer(modifier = Modifier.width(4.dp))
                Text(doctorName, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(typeIcon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(type, fontSize = 12.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
                    ) {
                        Text(
                            text = tag,
                            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("View File", color = Color(0xFF2196F3), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
            }
        }
    }
}
