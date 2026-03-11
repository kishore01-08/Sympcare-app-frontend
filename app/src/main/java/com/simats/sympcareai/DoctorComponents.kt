package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.*

@Composable
fun ActivePatientCard(
    name: String,
    time: String,
    status: String,
    statusColor: Color,
    statusTextColor: Color,
    initial: String,
    initialBg: Color,
    showCheckbox: Boolean = true, // Added to toggle checkbox visibility
    onMarkComplete: (() -> Unit)? = null, // Optional callback
    onClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }, 
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCheckbox) {
                Checkbox(
                    checked = status == "Completed",
                    onCheckedChange = { checked ->
                        if (checked && status != "Completed") {
                            onMarkComplete?.invoke()
                        }
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00BFA5))
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = time, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        color = statusTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundGradient: List<Color>? = null
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp), // Rounded square look
        colors = CardDefaults.cardColors(containerColor = if (backgroundGradient != null) Color.Transparent else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (backgroundGradient != null) {
                        Modifier.background(Brush.linearGradient(backgroundGradient))
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (backgroundGradient != null) Color.White.copy(alpha = 0.2f) else iconColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon, 
                            contentDescription = null, 
                            tint = if (backgroundGradient != null) Color.White else iconColor, 
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    color = if (backgroundGradient != null) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
