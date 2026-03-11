package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerScreen(
    onAcceptClick: () -> Unit,
    isDoctor: Boolean
) {
    var isChecked by remember { mutableStateOf(false) }

    val primaryColor = if (isDoctor) Color(0xFFE65100) else Color(0xFF009688)
    val headerColor = if (isDoctor) Color(0xFFFF9800) else Color(0xFF00C9B9)// Lighter variant for header background if needed, or stick to primary. Using Primary for consistency.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Important Notice",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Please read carefully",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Medical Disclaimer Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)), // Light Yellow
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Medical Disclaimer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SympCareAI is an AI-powered health information tool designed to provide general guidance only.",
                        fontSize = 14.sp,
                        color = Color(0xFF546E7A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This app is NOT a substitute for professional medical advice, diagnosis, or treatment.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Points
            DisclaimerItem(
                number = 1,
                title = "Not Medical Advice",
                description = "Information provided is for educational purposes and should not replace consultation with healthcare professionals."
            )
            Spacer(modifier = Modifier.height(16.dp))
            DisclaimerItem(
                number = 2,
                title = "Seek Emergency Care",
                description = "In case of emergency, call emergency services immediately. Do not rely solely on this app."
            )
            Spacer(modifier = Modifier.height(16.dp))
            DisclaimerItem(
                number = 3,
                title = "Consult Healthcare Providers",
                description = "Always consult with qualified healthcare providers for personalized medical advice."
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                )
                Text(
                    text = "I have read and understood the disclaimer",
                    fontSize = 14.sp,
                    color = Color(0xFF37474F)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accept Button
            Button(
                onClick = onAcceptClick,
                enabled = isChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Accept & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DisclaimerItem(number: Int, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE0F2F1), // Very light teal/gray
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF009688) // Always teal? Or adapted? Image shows Teal numbers.
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF546E7A)
            )
        }
    }
}
