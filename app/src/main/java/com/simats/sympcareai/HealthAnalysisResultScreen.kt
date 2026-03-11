package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HealthAnalysisResultScreen(
    score: Int = 68,
    onCloseClick: () -> Unit,
    onStartNewSessionClick: () -> Unit
) {
    val improvement = score - 60 // Simple mock logic: improved if > 60
    val status = when {
        score >= 80 -> "Improved"
        score >= 60 -> "Neutral"
        else -> "Needs Attention"
    }
    val statusColor = when {
        score >= 80 -> Color(0xFFE0F2F1)
        score >= 60 -> Color(0xFFFFF3E0)
        else -> Color(0xFFFFEBEE)
    }
    val statusTextColor = when {
        score >= 80 -> Color(0xFF009688)
        score >= 60 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Scaffold(
        containerColor = Color(0xFFFAF9F6)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onCloseClick,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✓", color = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Complete", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Health Analysis",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = "Your wellness report is ready",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-30).dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // HEALTH SCORE CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "HEALTH IMPROVEMENT",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.size(150.dp),
                                color = Color(0xFFE3F2FD),
                                strokeWidth = 12.dp,
                                trackColor = Color(0xFFE3F2FD),
                            )

                            CircularProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier.size(150.dp),
                                color = Color(0xFF2196F3),
                                strokeWidth = 12.dp,
                                strokeCap = StrokeCap.Round
                            )

                            Text(
                                "$score%",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Surface(
                            color = statusColor,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                val emoji = when(status) {
                                    "Improved" -> "📈"
                                    "Neutral" -> "📊"
                                    else -> "⚠️"
                                }
                                Text("$emoji $status", color = statusTextColor, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (improvement >= 0) "+$improvement% compared to last session" else "$improvement% compared to last session",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI SUMMARY
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            color = Color(0xFF2196F3),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("AI Health Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when {
                                    score >= 80 -> "Excellent progress! Your health indicators are strong. Keep up the good work and maintain your current routine."
                                    score >= 60 -> "You're maintaining steady health. Some areas show improvement while others need attention. Focus on consistent hydration and sleep."
                                    else -> "Your wellness score is lower than usual. We recommend focusing on restorative sleep and light physical activity this week."
                                },
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RECOMMENDATIONS CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("✓ Recommendations", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        RecommendationItem("1", "Maintain a consistent sleep schedule")
                        Spacer(modifier = Modifier.height(12.dp))
                        RecommendationItem("2", "Practice 10 minutes of meditation daily")
                        Spacer(modifier = Modifier.height(12.dp))
                        RecommendationItem("3", "Stay hydrated throughout the day")
                        Spacer(modifier = Modifier.height(12.dp))
                        RecommendationItem("4", "Include light exercise in your routine")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Complete", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartNewSessionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("+ Start New Session", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Composable
fun RecommendationItem(number: String, text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF00C853),
                shape = CircleShape,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(number, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 14.sp)
        }
    }
}
