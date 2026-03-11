package com.simats.sympcareai

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Timeout to navigate away
    LaunchedEffect(true) {
        delay(3000)
        onTimeout()
    }

    // Gradient Background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00E5FF), // Cyan-ish top
            Color(0xFF2979FF)  // Blue-ish bottom
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // White Circle Container with Shadow
            Surface(
                modifier = Modifier
                    .size(180.dp) // Slightly larger container for the logo
                    .shadow(elevation = 12.dp, shape = CircleShape),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Display the provided logo image
                    Image(
                        painter = painterResource(id = R.drawable.splash_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name "SympCare AI"
            Text(
                text = "SympCare AI",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = Offset(0f, 4f),
                        blurRadius = 8f
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Dots "..."
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                val dotColor = Color.White.copy(alpha = 0.8f)
                WaitingDot(color = dotColor, delayMillis = 0)
                WaitingDot(color = dotColor, delayMillis = 300)
                WaitingDot(color = dotColor, delayMillis = 600)
            }
        }
    }
}

@Composable
fun WaitingDot(color: Color, delayMillis: Int) {
    val alpha = remember { Animatable(0.2f) }
    
    LaunchedEffect(true) {
        delay(delayMillis.toLong())
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Text(
        text = ".",
        color = color.copy(alpha = alpha.value),
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}
