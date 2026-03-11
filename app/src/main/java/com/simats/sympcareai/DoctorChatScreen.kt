package com.simats.sympcareai

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorChatScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    // File Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Handle file selection (For now just log or TODO)
        // In a real app, this would upload the file
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header (Purple Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp) // Adjusted height
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Lightbulb, 
                            contentDescription = "AI", 
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Doctor AI Assistant",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Smart Medical Support",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Chat Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Initial AI Message
                DoctorChatBubble(
                    isUser = false,
                    content = {
                        Text(
                            text = "Hello Dr. Johnson! How can I assist you today?",
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )
                    },
                    timestamp = "10:30 AM"
                )

                // User Message
                DoctorChatBubble(
                    isUser = true,
                    content = {
                        Text(
                            text = "I need to analyze symptoms for patient PAT-10234. He has reported headache, dizziness, and fatigue.",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    },
                    timestamp = "10:31 AM"
                )

                // AI Response (Complex Card)
                DoctorChatBubble(
                    isUser = false,
                    content = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analysis Complete",
                                    color = Color(0xFF673AB7),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("POSSIBLE CONDITIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Condition Items
                            DoctorChatConditionItem(name = "Migraine", severity = "Moderate", severityColor = Color(0xFFFFCDD2), severityTextColor = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(8.dp))
                            DoctorChatConditionItem(name = "Dehydration", severity = "Mild", severityColor = Color(0xFFFFE0B2), severityTextColor = Color(0xFFF57C00))
                            Spacer(modifier = Modifier.height(8.dp))
                            DoctorChatConditionItem(name = "Stress-related Fatigue", severity = "Mild", severityColor = Color(0xFFFFE0B2), severityTextColor = Color(0xFFF57C00))
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("PRIORITY LEVEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFCDD2),
                                    modifier = Modifier.size(40.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE57373))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("3", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Moderate Priority", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Consultation within 24hrs", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    },
                    timestamp = "10:31 AM",
                    isCard = true
                )
                
                // AI Follow-up Text
                DoctorChatBubble(
                    isUser = false,
                    content = {
                        Text(
                            text = "Based on the symptoms, I've identified possible conditions. The patient requires consultation within 24 hours. Would you like me to generate a detailed report?",
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )
                    },
                    timestamp = "10:31 AM"
                )
            }

            // Input Area
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment Button with Dropdown logic
                    Box {
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.background(Color(0xFFF5F5F5), CircleShape).size(40.dp)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = Color.White
                        ) {
                            DropdownMenuItem(
                                text = { Text("Upload File") },
                                onClick = {
                                    expanded = false
                                    launcher.launch("*/*") // Launch file picker
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF6A5ACD))
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Ask AI about symptoms, reports...", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedBorderColor = Color(0xFFEEEEEE),
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            cursorColor = Color(0xFF6A5ACD) // Purple cursor
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Mic
                    IconButton(
                         onClick = { },
                         modifier = Modifier.background(Color(0xFFF5F5F5), CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    // Send
                    IconButton(
                        onClick = { },
                        modifier = Modifier.background(Color(0xFFE0E0E0), CircleShape).size(40.dp) // Disabled look for empty
                    ) {
                         Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorChatBubble(
    isUser: Boolean,
    content: @Composable () -> Unit,
    timestamp: String,
    isCard: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // AI Avatar
            Surface(
                shape = CircleShape,
                color = Color(0xFF9575CD), // Purple
                modifier = Modifier.size(32.dp).offset(y = 4.dp),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Surface(
                color = if (isUser) Color(0xFF6A5ACD) else if (isCard) Color.White else Color.White,
                shape = if (isUser) RoundedCornerShape(16.dp).copy(bottomEnd = CornerSize(0.dp))
                        else RoundedCornerShape(16.dp).copy(bottomStart = CornerSize(0.dp)),
                shadowElevation = if (isCard || !isUser) 2.dp else 4.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Box(modifier = Modifier.padding(if(isCard) 0.dp else 16.dp)) {
                    if (isCard) {
                        Column {
                            // Header matching design
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEDE7F6)) // Light Purple Header
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                     Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(16.dp))
                                     Spacer(modifier = Modifier.width(8.dp))
                                     Text(
                                         text = "Analysis Complete",
                                         color = Color(0xFF673AB7),
                                         fontWeight = FontWeight.Bold,
                                         fontSize = 14.sp
                                     )
                                 }
                            }
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("POSSIBLE CONDITIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                DoctorChatConditionItem(name = "Migraine", severity = "Moderate", severityColor = Color(0xFFFFCDD2), severityTextColor = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.height(8.dp))
                                DoctorChatConditionItem(name = "Dehydration", severity = "Mild", severityColor = Color(0xFFFFE0B2), severityTextColor = Color(0xFFF57C00))
                                Spacer(modifier = Modifier.height(8.dp))
                                DoctorChatConditionItem(name = "Stress-related Fatigue", severity = "Mild", severityColor = Color(0xFFFFE0B2), severityTextColor = Color(0xFFF57C00))
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("PRIORITY LEVEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFFCDD2),
                                        modifier = Modifier.size(40.dp),
                                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE57373))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("3", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Moderate Priority", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Consultation within 24hrs", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        content()
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = timestamp, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun DoctorChatConditionItem(name: String, severity: String, severityColor: Color, severityTextColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
             Box(modifier = Modifier.size(4.dp).background(Color.Black, CircleShape))
             Spacer(modifier = Modifier.width(8.dp))
             Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2D3436))
        }
        Surface(
            color = severityColor,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = severity,
                color = severityTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}