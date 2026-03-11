package com.simats.sympcareai

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.simats.sympcareai.data.response.DiseaseRisk
import com.simats.sympcareai.data.response.FileAnalysisResponse
import com.simats.sympcareai.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadHealthFileScreen(
    patientId: String,
    symptoms: List<String> = emptyList(),
    answers: Map<String, String> = emptyMap(),
    onBackClick: () -> Unit,
    onAnalyseClick: (uris: List<Uri>, description: String, category: String) -> Unit,
    onNavigateTo: (Screen) -> Unit,
    initialFileUris: List<Uri> = emptyList(),
    initialDescription: String = "",
    initialCategory: String = "Prescription"
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf(initialDescription) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedFileUris by remember { mutableStateOf<List<Uri>>(initialFileUris) }
    var isAttaching by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedFileUris = selectedFileUris + uris
    }

    // Helper to get real filename from Uri
    fun getFileName(uri: Uri): String {
        var name = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name.ifEmpty { uri.path?.substringAfterLast("/") ?: "Selected File" }
    }

    // getFileFromUri function remains same (used for debug/logic if needed, but here we just collect URIs)
    fun handleAttach() {
        if (selectedFileUris.isEmpty()) {
            errorMessage = "Please select at least one file."
            return
        }
        
        isAttaching = true
        // Short delay to simulate "processing" or "attaching" for UX
        onAnalyseClick(selectedFileUris, description, selectedCategory)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF00C9B9))
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.UploadHealthFile,
                onNavigateTo = onNavigateTo
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00C9B9))
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF00C9B9), modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Analyze Medical Documentation",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Get immediate AI insights from your reports",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Content Card
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-20).dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Upload Zone
                    Surface(
                        color = Color(0xFFE0F2F1),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00C9B9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 250.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00C9B9),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Tap to select files", fontWeight = FontWeight.Bold, color = Color(0xFF00695C))
                            Text("Supported formats: PDF, JPG, PNG", fontSize = 12.sp, color = Color(0xFF00695C))
                            Spacer(modifier = Modifier.height(16.dp))
                            // File List
                            selectedFileUris.forEach { uri ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF009688))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = getFileName(uri),
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = { selectedFileUris = selectedFileUris - uri }) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                                        }
                                    }
                                }
                            }

                            // Choose File Button (Always visible to add more)
                            Button(
                                onClick = { launcher.launch("*/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00C9B9)),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text(if (selectedFileUris.isEmpty()) "Choose Files" else "Add More Files", color = Color(0xFF00C9B9), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // File Category
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Text(
                            "| File Category",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F),
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = selectedCategory == "Prescription",
                            onClick = { selectedCategory = "Prescription" },
                            label = { Text("Prescription") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2962FF),
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedCategory == "Lab Report",
                            onClick = { selectedCategory = "Lab Report" },
                            label = { Text("Lab Report") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2962FF),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedCategory == "Other",
                            onClick = { selectedCategory = "Other" },
                            label = { Text("Other") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2962FF),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // File Description
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                         Text(
                            "| File Description",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F),
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe the file (e.g., Blood test results...)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Button(
                        onClick = { handleAttach() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9B9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyse")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                    ) {
                         Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                         Spacer(modifier = Modifier.width(8.dp))
                         Text("Cancel", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Secure Upload Notice
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Secure", tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Instant Analysis: Your files will be securely processed and analyzed immediately.",
                                fontSize = 12.sp,
                                color = Color(0xFF1565C0),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
