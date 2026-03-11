package com.simats.sympcareai

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.data.response.FileAnalysisResponse
import com.simats.sympcareai.data.response.FileUploadResponse
import com.simats.sympcareai.data.response.GenericStatusResponse
import com.simats.sympcareai.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalReportResultScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    patientId: String,
    uris: List<Uri>,
    description: String,
    category: String,
    reportId: Int = -1
) {
    val context = LocalContext.current
    var fileResult by remember { mutableStateOf<FileAnalysisResponse?>(null) }
    var currentReportId by remember { mutableStateOf(reportId) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            var fileName = "temp_file"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
            val tempFile = File(context.cacheDir, "upload_$fileName")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    LaunchedEffect(Unit) {
        try {
            if (uris.isNotEmpty()) {
                var latestReportId = currentReportId
                
                // Upload files sequentially
                for (uri in uris) {
                    val file = getFileFromUri(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
                        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                        val patientIdPart = patientId.toRequestBody("text/plain".toMediaTypeOrNull())

                        val uploadResponse = RetrofitClient.apiService.uploadReport(patientIdPart, filePart)
                        if (uploadResponse.isSuccessful) {
                            latestReportId = uploadResponse.body()?.fileId ?: latestReportId
                        } else {
                            errorMessage = "Upload failed for ${file.name}: ${uploadResponse.code()}"
                            isLoading = false
                            return@LaunchedEffect
                        }
                    }
                }
                
                currentReportId = latestReportId
                
                if (currentReportId != -1) {
                    // Trigger analysis
                    val analysisResponse = RetrofitClient.apiService.runAnalysis(currentReportId)
                    if (analysisResponse.isSuccessful) {
                        fileResult = analysisResponse.body()
                    } else {
                        errorMessage = "Analysis failed: ${analysisResponse.code()}"
                    }
                } else {
                    errorMessage = "No report ID generated after upload."
                }
                isLoading = false
            } else if (reportId != -1) {
                // No new files, fetch historical analysis result
                val response = RetrofitClient.apiService.getReportAnalysis(reportId)
                if (response.isSuccessful) {
                    fileResult = response.body()
                } else {
                    errorMessage = "Historical analysis fetch failed: ${response.code()}"
                }
                isLoading = false
            } else {
                isLoading = false
                errorMessage = "No files or report ID provided."
            }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Error: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analysis Result", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00C9B9))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (uris.isNotEmpty()) "AI is analyzing your report..." else "Fetching your health record...",
                        color = Color.Gray
                    )
                }
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(errorMessage!!, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Triage & Severity
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val triage = fileResult?.triage ?: 3
                    val severity = fileResult?.severityScore ?: 0.0f
                    
                    ResultBox(
                        title = "Triage Level",
                        value = triage.toString(),
                        color = when(triage) {
                            1 -> Color.Red
                            2 -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    ResultBox(
                        title = "Severity Score",
                        value = "${(severity * 100).toInt()}%",
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Detailed Report
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF00C9B9))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Analysis Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = fileResult?.report ?: "No analysis available.",
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Disease Risks
                if (!fileResult?.possibleDiseases.isNullOrEmpty()) {
                    Text("Potential Insights", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    fileResult?.possibleDiseases?.forEach { disease ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if ((disease.probability ?: 0f) > 0.5) Color.Red else Color(0xFFFF9800)))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(disease.name, fontWeight = FontWeight.Bold)
                                    Text("Identified in report analysis", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onNavigateTo(Screen.PatientHome) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9B9)),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Back to Home")
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ResultBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
