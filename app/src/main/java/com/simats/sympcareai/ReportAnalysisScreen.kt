package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.TextView
import androidx.compose.ui.viewinterop.AndroidView
import com.simats.sympcareai.network.RetrofitClient
import com.simats.sympcareai.data.response.*
import com.simats.sympcareai.utils.DateTimeUtils
import androidx.compose.ui.draw.clip
import com.simats.sympcareai.ui.TriageComponent
import io.noties.markwon.Markwon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAnalysisScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    reportId: Int = -1
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var fileResult by remember { mutableStateOf<FileAnalysisResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val markwon = remember { Markwon.create(context) }

    fun formatDateTime(isoString: String?): String {
        return DateTimeUtils.formatToKolkataTime(isoString)
    }

    LaunchedEffect(reportId) {
        if (reportId != -1 && reportId != 0) {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getReportAnalysis(reportId)
                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    fileResult = response.body()
                } else {
                    errorMessage = "Failed to fetch analysis result"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Network error: ${e.message}"
            }
        } else {
            isLoading = false
            errorMessage = "No report ID provided for analysis."
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Medical Report Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF00C9B9))
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF009688))
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Uploaded File
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color(0xFF00C9B9), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Uploaded Document", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            val fileName = fileResult?.fileUrl?.substringAfterLast("/")?.substringBefore("?") ?: "Report File"
                            Text(fileName, fontSize = 12.sp, color = Color.Gray)
                        }
                        if (fileResult?.fileUrl != null) {
                            TextButton(
                                onClick = {
                                    val url = fileResult?.fileUrl!!
                                    // Ensure URL is absolute. Backend often returns relative paths for media.
                                    val fullUrl = if (url.startsWith("http")) url else "http://10.31.167.156:8000$url"
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(fullUrl))
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("Open File", color = Color(0xFF00C9B9), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section 2: Uploaded Time
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Uploaded At", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                            Text(
                                text = formatDateTime(fileResult?.uploadedAt),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Section 3: Triage & Severity
                if (fileResult?.triage != null) {
                    val triage = fileResult?.triage
                    val triageColor = when (triage) {
                        1 -> Color(0xFFD32F2F)
                        2 -> Color(0xFFEF6C00)
                        3 -> Color(0xFF388E3C)
                        else -> Color.Gray
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = triageColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Triage Level", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = triageColor)
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            TriageComponent(triage = triage)
                            
                            if (fileResult?.severityScore != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Severity Score", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val severity = fileResult?.severityScore ?: 0.0f
                                    LinearProgressIndicator(
                                        progress = { severity },
                                        modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFFD32F2F),
                                        trackColor = Color(0xFFFFEBEE)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("${(severity * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                }

                // Section 4: Report Analysis
                Text(
                    "AI Analysis Summary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        AndroidView(
                            factory = { context -> TextView(context).apply { 
                                setTextColor(android.graphics.Color.BLACK)
                                textSize = 15f
                            } },
                            update = { textView ->
                                markwon.setMarkdown(textView, fileResult?.report ?: "No analysis content available.")
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
