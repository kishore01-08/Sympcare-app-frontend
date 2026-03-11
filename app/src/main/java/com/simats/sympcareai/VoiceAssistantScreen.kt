package com.simats.sympcareai

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.simats.sympcareai.network.RetrofitClient
import com.simats.sympcareai.data.response.QuestionsResponse
import com.simats.sympcareai.data.response.AIAnalysisResponse
import kotlinx.coroutines.launch
import retrofit2.Response

data class VoiceMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VoiceAssistantScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onAnalyseClick: (AIAnalysisResponse) -> Unit,
    selectedSymptoms: List<String> = emptyList(),
    patientId: String = "",
    sessionId: Int = -1
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    
    val messages = remember { mutableStateListOf<VoiceMessage>() }
    val listState = rememberLazyListState()

    var questions by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var isLoadingQuestions by remember { mutableStateOf(true) }
    
    val collectedAnswers = remember { mutableStateMapOf<String, String>() }

    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisError by remember { mutableStateOf<String?>(null) }

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
                    onAnalyseClick(response.body()!!)
                } else {
                    analysisError = "Analysis failed: ${response.code()}"
                }
            } catch (e: Exception) {
                isAnalyzing = false
                analysisError = "Network error: ${e.message}"
            }
        }
    }

    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    fun speakAndAddMessage(text: String) {
        if (!messages.any { !it.isUser && it.text == text }) { 
             messages.add(VoiceMessage(text, isUser = false))
             speakText(tts, text)
        }
    }

    LaunchedEffect(Unit) {
        val request = mapOf(
            "patient_id" to patientId,
            "symptoms" to selectedSymptoms
        )
        isLoadingQuestions = true
        try {
            val response = RetrofitClient.apiService.getQuestions(request)
            if (response.isSuccessful) {
                questions = response.body()?.questions ?: emptyList()
            }
        } catch (e: Exception) {
            // Ignore failure for questions
        }
        isLoadingQuestions = false
    }

    LaunchedEffect(isLoadingQuestions) {
        if (!isLoadingQuestions && tts == null) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    if (messages.isEmpty() && questions.isNotEmpty()) {
                        val symptomText = if (selectedSymptoms.isNotEmpty()) " for ${selectedSymptoms.joinToString(", ")}" else ""
                        val initialGreeting = "Hello! I'm here to help with your symptoms$symptomText. ${questions[0]}"
                        speakAndAddMessage(initialGreeting)
                    }
                }
            }
        }
    }
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                startListening(context, speechRecognizer) { isListening = true }
            } else {
                Toast.makeText(context, "Microphone permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun processUserResponse(text: String) {
        if (text.isNotBlank()) {
            messages.add(VoiceMessage(text, isUser = true))
            
            if (currentQuestionIndex < questions.size) {
                val q = questions[currentQuestionIndex]
                when {
                    q.contains("severe", ignoreCase = true) || q.contains("1 to 10", ignoreCase = true) -> collectedAnswers["pain"] = text
                    q.contains("how many days", ignoreCase = true) || q.contains("how long", ignoreCase = true) -> collectedAnswers["days"] = text
                    else -> collectedAnswers["q_${currentQuestionIndex}"] = text
                }
            }

            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                 val nextQuestion = questions[currentQuestionIndex]
                 speakAndAddMessage(nextQuestion)
            } else {
                speakAndAddMessage("Thank you. I have gathered all the information needed for analysis. Please click Analyse to view your report.")
            }
        }
    }

    DisposableEffect(Unit) {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer = recognizer
        
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) { isListening = false }
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) { processUserResponse(matches[0]) }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        
        recognizer.setRecognitionListener(listener)

        onDispose {
            recognizer.destroy()
            tts?.stop()
            tts?.shutdown()
        }
    }

    if (isListening) {
        AlertDialog(
            onDismissRequest = {
                stopListening(speechRecognizer)
                isListening = false
            },
            icon = { Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF009688)) },
            title = { Text(text = "Listening...") },
            text = { Text(text = "Please speak now. Tap Stop to finish.", modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    stopListening(speechRecognizer)
                    isListening = false
                }) { Text("Stop") }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp).imePadding(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!isListening) {
                        Text(text = "Tap to Speak", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    IconButton(
                        onClick = {
                            if (isListening) {
                                stopListening(speechRecognizer)
                                isListening = false
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier.size(64.dp).background(Color.White, CircleShape).border(1.dp, Color(0xFF009688), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = if (isListening) Color.Red else Color(0xFF009688),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(
                brush = Brush.verticalGradient(colors = listOf(Color(0xFF6E48AA), Color(0xFF9D50BB).copy(alpha = 0.1f)))
            )
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Voice Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text(if (isListening) "Listening..." else "Online", fontSize = 12.sp, color = if (isListening) Color(0xFF00E676) else Color.White.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { performAnalysis() },
                        enabled = !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                        } else {
                            Text("Analyse", color = Color.Black, fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (selectedSymptoms.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        Text(text = "Selected Context:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(bottom = 8.dp))
                        androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                            selectedSymptoms.forEach { symptom -> 
                                SuggestionChip(onClick = {}, label = { Text(symptom, fontSize = 10.sp, color = Color.White) }, colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White.copy(alpha = 0.2f)), border = null)
                            }
                        }
                    }
                }

                LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(messages) { message ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start) {
                            Surface(
                                color = if (message.isUser) Color(0xFF009688) else Color.White,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (message.isUser) 16.dp else 4.dp, bottomEnd = if (message.isUser) 4.dp else 16.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) { Text(text = message.text, modifier = Modifier.padding(16.dp), color = if (message.isUser) Color.White else Color.Black) }
                        }
                    }

                    if (isAnalyzing) {
                        item { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Box(modifier = Modifier.padding(16.dp)) { CircularProgressIndicator(color = Color.White) } } }
                    }

                    if (analysisError != null) {
                        item {
                            Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text(text = analysisError!!, color = Color.Red, modifier = Modifier.padding(16.dp), fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun startListening(context: Context, speechRecognizer: SpeechRecognizer?, onListeningStarted: () -> Unit) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }
    speechRecognizer?.startListening(intent)
    onListeningStarted()
}

private fun stopListening(speechRecognizer: SpeechRecognizer?) { speechRecognizer?.stopListening() }
private fun speakText(tts: TextToSpeech?, text: String) { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }
