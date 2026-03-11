package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedbackScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    var rating by remember { mutableIntStateOf(4) } // Default 4 stars
    var feedbackText by remember { mutableStateOf("") }
    val quickTags = listOf("Accurate answers", "Easy to use", "Fast response", "Needs improvement", "Helpful suggestions")
    val selectedTags = remember { mutableStateListOf<String>() }
    var showSessionEndedDialog by remember { mutableStateOf(false) }

    if (showSessionEndedDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissal without action if strict, or allow */ },
            text = { Text("Thank you for your feedback! Your session has been successfully recorded.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        showSessionEndedDialog = false
                        onSubmitClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9B9))
                ) {
                    Text("Go to Home")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.PatientHome, // heuristic
                onNavigateTo = onNavigateTo
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00C9B9))
                    .padding(vertical = 24.dp, horizontal = 16.dp)
            ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                     Row(
                         modifier = Modifier.fillMaxWidth(),
                         horizontalArrangement = Arrangement.SpaceBetween,
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         IconButton(onClick = onBackClick) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                         }
                         Surface(
                             shape = CircleShape,
                             color = Color.White,
                             modifier = Modifier.size(48.dp)
                         ) {
                             Box(contentAlignment = Alignment.Center) {
                                  Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF00C9B9))
                             }
                         }
                         IconButton(onClick = {}) {
                              Icon(Icons.Default.StarBorder, contentDescription = null, tint = Color.White) // Placeholder functionality
                         }
                     }
                     Spacer(modifier = Modifier.height(16.dp))
                     Text("Share Your Feedback", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                     Text("Help us improve SympCareAI", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                 }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                
                // Rating Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                         Text("| How was your experience?", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800), modifier = Modifier.fillMaxWidth()) // Orange accent
                         Spacer(modifier = Modifier.height(16.dp))
                         
                         // Stars
                         Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                             repeat(5) { index ->
                                 Icon(
                                     imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                     contentDescription = "Star ${index + 1}",
                                     tint = Color(0xFFFFC107),
                                     modifier = Modifier
                                         .size(40.dp)
                                         .clickable { rating = index + 1 }
                                 )
                             }
                         }
                         Spacer(modifier = Modifier.height(16.dp))
                         Text("Great! \uD83D\uDE0A", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold) // Simple logic for demo
                         
                         Spacer(modifier = Modifier.height(24.dp))
                         Text("Or choose a quick reaction", fontSize = 12.sp, color = Color.Gray)
                         Spacer(modifier = Modifier.height(12.dp))
                         
                         // Emojis (Visual representation only using text for simplicity or custom icons)
                         Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                             EmojiReaction("Poor", "\uD83D\uDE1E", false)
                             EmojiReaction("Average", "\uD83D\uDE10", false)
                             EmojiReaction("Good", "\uD83D\uDE42", false)
                             EmojiReaction("Excellent", "\uD83D\uDE04", true) // Selected style demo
                         }
                    }
                }

                // Text Input
                Column {
                     Text("| Tell us more", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7), modifier = Modifier.fillMaxWidth())
                     Spacer(modifier = Modifier.height(12.dp))
                     OutlinedTextField(
                         value = feedbackText,
                         onValueChange = { feedbackText = it },
                         placeholder = { Text("Describe your experience, suggestions, or issues...") },
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(150.dp),
                         shape = RoundedCornerShape(12.dp),
                         colors = OutlinedTextFieldDefaults.colors(
                             focusedTextColor = Color.Black,
                             unfocusedTextColor = Color.Black,
                             focusedContainerColor = Color.White,
                             unfocusedContainerColor = Color.White,
                             focusedBorderColor = Color(0xFFE0E0E0),
                             unfocusedBorderColor = Color(0xFFE0E0E0)
                         )
                     )
                     Spacer(modifier = Modifier.height(8.dp))
                     Text("Your feedback is valuable to us", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
                }

                // Quick Tags
                Column {
                    Text("| Quick Feedback", fontWeight = FontWeight.Bold, color = Color(0xFF009688))
                     Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickTags.forEach { tag ->
                            val isSelected = selectedTags.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                    if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                },
                                label = { Text(tag) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00C9B9),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) Color.Transparent else Color(0xFFE0E0E0),
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                // Related Session
                Column {
                    Text("| Related Session", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1C4E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Surface(color = Color.White, shape = CircleShape, modifier = Modifier.size(40.dp)) {
                                 Box(contentAlignment = Alignment.Center) {
                                     Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF673AB7))
                                 }
                             }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Headache consultation", fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
                                Text("Voice Assistant • 2 mins ago", fontSize = 12.sp, color = Color(0xFF7E57C2))
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFB39DDB))
                        }
                    }
                }

                // Actions
                Button(
                    onClick = { showSessionEndedDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9B9)),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Feedback")
                }

                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)) // Light gray border
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel", color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EmojiReaction(label: String, emoji: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFFAFAFA),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(60.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00C9B9)) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 28.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = if (isSelected) Color(0xFF009688) else Color.Gray)
    }
}
