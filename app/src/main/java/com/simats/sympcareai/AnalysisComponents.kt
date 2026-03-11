package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.simats.sympcareai.data.response.AIAnalysisResponse
import com.simats.sympcareai.ai_engine.AIEngine

@Composable
fun SectionCard(title: String, color: Color, content: @Composable () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
             Box(modifier = Modifier.width(4.dp).height(16.dp).background(color, RoundedCornerShape(2.dp)))
             Spacer(modifier = Modifier.width(8.dp))
             Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ConditionItem(title: String, risk: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
             Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
             Spacer(modifier = Modifier.height(8.dp))
             Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
             Text(risk, fontSize = 12.sp, color = color)
        }
    }
}

@Composable
fun RecommendationItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.8f))
    }
}

@Composable
fun AnalysisResultView(result: AIAnalysisResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val triageColor = AIEngine.getTriageColor(result.triage ?: 3)
                Box(modifier = Modifier.size(12.dp).background(triageColor, CircleShape))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AI System Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Primary Condition
            Text(
                text = "Most Likely Condition:",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = result.mainDisease ?: "General Concern",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3F51B5)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Triage & Priority
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Triage Status", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = AIEngine.getTriageDescription(result.triage ?: 3),
                        fontWeight = FontWeight.Bold,
                        color = AIEngine.getTriageColor(result.triage ?: 3),
                        fontSize = 14.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Severity Score", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${((result.severityScore ?: 0f) * 100).toInt()}%",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
            
            if (!result.possibleDiseases.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Differential Analysis", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                result.possibleDiseases?.take(3)?.forEach { disease ->
                    AnalysisConditionItemSimple(
                        name = disease.name,
                        prob = AIEngine.formatProbability(disease.probability)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun AnalysisConditionItemSimple(name: String, prob: String) {
    Surface(
        color = Color(0xFFF9F9F9),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontSize = 13.sp, color = Color.Black)
            Text(text = prob, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF009688))
        }
    }
}
