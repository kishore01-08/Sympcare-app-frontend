package com.simats.sympcareai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.simats.sympcareai.network.RetrofitClient
import com.simats.sympcareai.data.response.QuestionsResponse
import com.simats.sympcareai.data.response.AIAnalysisResponse
import kotlinx.coroutines.launch
import retrofit2.Response

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onFinishClick: (AIAnalysisResponse) -> Unit,
    isReadOnly: Boolean = false,
    selectedSymptoms: List<String> = emptyList(),
    patientId: String = "",
    sessionId: Int = -1,
    history: com.simats.sympcareai.data.response.ChatHistoryDTO? = null
) {
    var message by remember { mutableStateOf("") }
    var showAllSymptomsPopup by remember { mutableStateOf(false) }
    
    // Conversation State
    val messages = remember<androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage>> { mutableStateListOf() }
    val listState = rememberLazyListState()
    
    // Symptoms to display (either passed in or from history)
    val displaySymptoms = remember(selectedSymptoms, history) {
        history?.symptoms ?: selectedSymptoms
    }
    
    // Backend Questions State
    var questions by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var isLoadingQuestions by remember { mutableStateOf(history == null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisError by remember { mutableStateOf<String?>(null) }
    
    // Collected Answers for Analysis
    val collectedAnswers = remember { mutableStateMapOf<String, String>() }
    
    val scope = rememberCoroutineScope()

    fun performAnalysis() {
        isAnalyzing = true
        analysisError = null
        
        val request = mapOf(
            "patient_id" to patientId,
            "symptoms" to selectedSymptoms,
            "answers" to collectedAnswers.toMap()
        )
        
        scope.launch {
            try {
                val response = RetrofitClient.apiService.analyzeAI(request)
                isAnalyzing = false
                if (response.isSuccessful && response.body() != null) {
                    onFinishClick(response.body()!!)
                } else {
                    analysisError = "Analysis failed: ${response.code()}"
                }
            } catch (e: Exception) {
                isAnalyzing = false
                analysisError = "Network error: ${e.message}"
            }
        }
    }

    // Fetch questions from backend OR populate from history
    LaunchedEffect(Unit) {
        if (history != null) {
            messages.add(ChatMessage("Hello! Here is the record of your consultation for ${history.disease} on ${com.simats.sympcareai.utils.DateTimeUtils.formatToKolkataTime(history.date)}.", isUser = false))
            
            history.answers.forEach { (q, a) ->
                // Skip legacy/helper keys used for backend analysis only
                if (q != "pain" && q != "days") {
                    // q is the actual question text now
                    messages.add(ChatMessage(q, isUser = false))
                    messages.add(ChatMessage(a, isUser = true))
                }
            }
            
            messages.add(ChatMessage("Analysis Result: ${history.disease}", isUser = false))
            val triageText = when(history.triage) {
                1 -> "High"
                2 -> "Moderate"
                3 -> "Low"
                else -> "Unknown"
            }
            messages.add(ChatMessage("Triage: $triageText", isUser = false))
            
            isLoadingQuestions = false
        } else if (!isReadOnly) {
            val request = mapOf(
                "patient_id" to patientId,
                "symptoms" to selectedSymptoms
            )
            isLoadingQuestions = false
            try {
                val response = RetrofitClient.apiService.getQuestions(request)
                if (response.isSuccessful) {
                    questions = response.body()?.questions ?: emptyList()
                    if (questions.isNotEmpty()) {
                        val symptomText = if (selectedSymptoms.isNotEmpty()) " for ${selectedSymptoms.joinToString(", ")}" else ""
                        messages.add(ChatMessage("Hello! I'm here to help with your symptoms$symptomText. ${questions[0]}", isUser = false))
                    } else {
                        messages.add(ChatMessage("Hello! I'm here to help. How can I assist you today?", isUser = false))
                    }
                } else {
                    messages.add(ChatMessage("Hello! I'm having trouble connecting to my knowledge base. Please try describing your symptoms anyway.", isUser = false))
                }
            } catch (e: Exception) {
                messages.add(ChatMessage("Network error. Please check your connection.", isUser = false))
            }
        }
    }

    // Auto-scroll
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage() {
        if (message.isNotBlank()) {
            val userMsg = message
            messages.add(ChatMessage(userMsg, isUser = true))
            
            // Map questions to keys for analysis and history
            if (currentQuestionIndex < questions.size) {
                val q = questions[currentQuestionIndex]
                // Store with actual question text as key for history display
                collectedAnswers[q] = userMsg
                
                // Also store with specific keys for legacy backend analysis logic
                if (q.contains("severe", ignoreCase = true) || q.contains("1 to 10", ignoreCase = true)) {
                    collectedAnswers["pain"] = userMsg
                } else if (q.contains("how many days", ignoreCase = true) || q.contains("how long", ignoreCase = true)) {
                    collectedAnswers["days"] = userMsg
                }
            }

            message = ""

            // AI Response Logic
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                val nextQ = questions[currentQuestionIndex]
                messages.add(ChatMessage(nextQ, isUser = false))
            } else {
                messages.add(ChatMessage("Thank you. I have gathered all the information needed for analysis.", isUser = false))
                messages.add(ChatMessage("Please click 'Analyze' to generate your health report.", isUser = false))
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (!isReadOnly) {
                AppBottomNavigationBar(
                    currentScreen = Screen.Chat,
                    onNavigateTo = onNavigateTo
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF5F5F5))) {
        // App Bar
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SympCareAI", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Text("Health AI Online", fontSize = 12.sp, color = Color(0xFF009688))
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.DarkGray)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
            actions = {
                if (!isReadOnly) {
                    Button(
                        onClick = { performAnalysis() },
                        enabled = !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.padding(end = 8.dp).height(36.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Analyze", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        )

        // Chat Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selected Symptoms Display
            if (displaySymptoms.isNotEmpty()) {
                Surface(
                    onClick = { showAllSymptomsPopup = true },
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Selected Symptoms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF009688),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            displaySymptoms.forEach { symptom ->
                                Surface(
                                    color = Color(0xFFE0F2F1),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = symptom,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        color = Color(0xFF009688)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reference to List State for auto-scroll
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        ChatBubble(text = msg.text, isUser = msg.isUser)
                    }
                }

                if (analysisError != null) {
                    item {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = analysisError!!,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Input Area - Only show if not read-only
        if (!isReadOnly) {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Describe your symptoms...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = { sendMessage() }) {
                       Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF009688))
                    }
                }
            }
        }
    }

    // Full List Popup
    if (showAllSymptomsPopup) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showAllSymptomsPopup = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF5F5F5)
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Selected Symptoms", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                        navigationIcon = {
                            IconButton(onClick = { showAllSymptomsPopup = false }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(selectedSymptoms) { symptom ->
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF009688),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = symptom, fontSize = 16.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
}
