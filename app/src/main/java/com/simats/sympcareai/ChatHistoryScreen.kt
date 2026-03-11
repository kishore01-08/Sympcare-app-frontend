package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.utils.DateTimeUtils
import androidx.compose.runtime.*
import com.simats.sympcareai.data.response.ChatHistoryDTO
import com.simats.sympcareai.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    onBackClick: () -> Unit,
    onChatClick: (com.simats.sympcareai.data.response.ChatHistoryDTO) -> Unit,
    onNavigateTo: (Screen) -> Unit,
    patientId: String
) {
    var historyList by remember { mutableStateOf<List<com.simats.sympcareai.data.response.ChatHistoryDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        try {
            val response = com.simats.sympcareai.network.RetrofitClient.apiService.getChatHistory(patientId)
            if (response.isSuccessful) {
                historyList = response.body()?.history ?: emptyList()
            } else {
                errorMessage = "Failed to load history: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Network error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.ChatHistory,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Item
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                            )
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chat History",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0072FF))
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage!!, color = Color.Red)
                    }
                }
            } else if (historyList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No chat history found", color = Color.Gray)
                    }
                }
            } else {
                // Chat List Items
                items(historyList) { chat ->
                    ChatHistoryCardDTO(chat = chat, onClick = { onChatClick(chat) })
                }
            }
        }
    }
}

@Composable
fun ChatHistoryCardDTO(chat: com.simats.sympcareai.data.response.ChatHistoryDTO, onClick: () -> Unit) {
    val triageColor = when(chat.triage) {
        1 -> Color(0xFFD32F2F) // High
        2 -> Color(0xFFEF6C00) // Moderate
        3 -> Color(0xFF388E3C) // Low
        else -> Color.Gray
    }
    val triageBg = triageColor.copy(alpha = 0.1f)
    val triageLabel = when(chat.triage) {
        1 -> "High Risk"
        2 -> "Moderate Risk"
        3 -> "Low Risk"
        else -> "Unknown"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Date & Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.simats.sympcareai.utils.DateTimeUtils.formatToKolkataTime(chat.date, "dd MMM yyyy, hh:mm a"),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.LightGray.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Disease Name
            Text(
                text = chat.disease,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Symptoms List Snippet 
            Text(
                text = "Symptoms: ${chat.symptoms.joinToString(", ")}",
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Containers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Triage Container
                Surface(
                    color = triageBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Triage Level",
                            fontSize = 10.sp,
                            color = triageColor.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = triageLabel,
                            color = triageColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Severity Container
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Severity Score",
                            fontSize = 10.sp,
                            color = Color(0xFF1976D2).copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(chat.severityScore * 100).toInt()}%",
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
