package com.simats.sympcareai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TriageComponent(triage: Int?) {
    if (triage == null) return

    val (levelText, description, themeColor, bgColor) = when (triage) {
        1 -> Quadruple(
            "Level 1 – IMMEDIATE: Life-threatening.",
            "You must consult a doctor or go to the nearest emergency department right away. Call emergency services without delay.",
            Color(0xFFD32F2F),
            Color(0xFFFFEBEE)
        )
        2 -> Quadruple(
            "Level 2 – EMERGENCY: Could become life-threatening.",
            "Visit a hospital or emergency clinic as soon as possible. Do not wait for symptoms to worsen.",
            Color(0xFFEF6C00),
            Color(0xFFFFF3E0)
        )
        3 -> Quadruple(
            "Level 3 – URGENT: Not life-threatening.",
            "Medical evaluation is needed soon, but it is not an immediate emergency. Schedule a same-day or next-day appointment.",
            Color(0xFF388E3C),
            Color(0xFFE8F5E9)
        )
        else -> Quadruple(
            "UNKNOWN",
            "Triage level not assessed or unknown.",
            Color.Gray,
            Color(0xFFF5F5F5)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Colored Badge
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, themeColor.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = levelText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = themeColor,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Instruction Text
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

// Simple data wrapper for 4 values
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
