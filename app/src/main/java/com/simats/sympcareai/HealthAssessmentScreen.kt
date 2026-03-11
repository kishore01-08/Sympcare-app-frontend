package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
fun HealthAssessmentScreen(
    onBackClick: () -> Unit,
    onComplete: (Int) -> Unit // Passes the final score
) {
    val questions = remember { 
        HealthDataStore.fullQuestionList.shuffled().take(6) 
    }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<HealthOption?>(null) }
    var totalScore by remember { mutableStateOf(0) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    Scaffold(
        containerColor = Color(0xFFFAF9F6)
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
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2196F3), Color(0xFF009688))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                         Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Quick Health Check",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Regular checkups help track your wellness",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Question Card
            if (currentQuestion != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-40).dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Card(
                         colors = CardDefaults.cardColors(containerColor = Color.White),
                         shape = RoundedCornerShape(24.dp),
                         elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                         modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Question Icon
                             Surface(
                                color = Color(0xFF2196F3),
                                shape = CircleShape,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("?", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = currentQuestion.text,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            // Options
                            currentQuestion.options.forEach { option ->
                                AssessmentOption(
                                    emoji = option.emoji,
                                    label = option.label,
                                    selected = selectedOption == option,
                                    onSelect = { selectedOption = option }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = {
                                    selectedOption?.let {
                                        totalScore += it.score
                                        if (currentQuestionIndex < questions.size - 1) {
                                            currentQuestionIndex++
                                            selectedOption = null
                                        } else {
                                            // Scale score to percentage (max score is 5*5=25)
                                            val finalPercentage = (totalScore * 100) / (questions.size * 5)
                                            onComplete(finalPercentage)
                                        }
                                    }
                                },
                                enabled = selectedOption != null,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedOption != null) Color(0xFF2196F3) else Color(0xFFEEEEEE)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                 Text(
                                     text = if (currentQuestionIndex < questions.size - 1) "Next" else "Finish",
                                     color = if (selectedOption != null) Color.White else Color.Gray
                                 )
                                 Spacer(modifier = Modifier.width(8.dp))
                                 Icon(
                                     imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                     contentDescription = null,
                                     tint = if (selectedOption != null) Color.White else Color.Gray
                                 )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AssessmentOption(
    emoji: String,
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3)) else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2196F3))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
