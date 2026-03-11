package com.simats.sympcareai

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Surface(
        color = if (isUser) Color(0xFF009688) else Color.White,
        shape = if (isUser) RoundedCornerShape(16.dp).copy(bottomEnd = CornerSize(0.dp))
                else RoundedCornerShape(16.dp).copy(bottomStart = CornerSize(0.dp)),
        shadowElevation = 2.dp,
        modifier = Modifier.widthIn(max = 280.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = if (isUser) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}
