package com.simats.sympcareai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.network.RetrofitClient

@Composable
fun PatientHomeScreen(
    patientName: String,
    patientId: String,
    currentSessionId: Int = -1,
    refreshTrigger: Int = 0,
    onChatClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onHealthReportClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onSessionCreated: (Int) -> Unit
) {
    val context = LocalContext.current
    var profilePicUrl by remember { mutableStateOf<String?>(null) }
    
    // Fetch profile to get image
    LaunchedEffect(patientId, refreshTrigger) {
        if (patientId.isNotEmpty()) {
            try {
                val request = mapOf("patient_id" to patientId, "t" to System.currentTimeMillis().toString())
                val response = com.simats.sympcareai.network.RetrofitClient.apiService.getHealthProfile(request)
                if (response.isSuccessful) {
                    profilePicUrl = response.body()?.profilePicture
                }
            } catch (e: Exception) {
                // Ignore failure for minimal impact on home screen
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.PatientHome,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Icon (moved to left)
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2F1),
                        modifier = Modifier
                            .size(50.dp)
                            .clickable(onClick = onProfileClick)
                    ) {
                        if (profilePicUrl != null) {
                            val imageUrl = if (profilePicUrl!!.startsWith("http")) profilePicUrl else "http://10.0.2.2:8000$profilePicUrl"
                            coil.compose.AsyncImage(
                                model = imageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF009688), modifier = Modifier.padding(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Welcome, $patientName!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "How are you feeling today?",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start New Consultation Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable(onClick = { onChatClick() })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = "Start New Consultation",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Talk to AI about your symptoms",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voice & Report Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Voice Assistant
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable(onClick = { onNavigateTo(Screen.VoiceSymptomSelection) })
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Icon(
//                                imageVector = Icons.Default.Mic, // Need Mic icon
//                                contentDescription = null,
//                                tint = Color.White,
//                                modifier = Modifier.size(32.dp)
//                            )
                            // Using Home as placeholder for Mic if not available, but usually Mic is standard.
                            // Let's use generic placeholder text if icon issue, but Material Icons usually has Mic.
                            // Assuming Icons.Default.Mic exists or similar.
                             Text("🎤", fontSize = 32.sp) // Emoji as quick placeholder or standard icon
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Voice Assistant",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Health Report
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable(onClick = onHealthReportClick)
                ) {
                     Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFF09819), Color(0xFFEDDE5D))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📄", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Health Report",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Health Tip
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Today's Health Tip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val todaysTip = HealthTipsManager.getTodayTip()

                    Text(
                        text = todaysTip,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

             Spacer(modifier = Modifier.height(16.dp))

             // Report Analysis
             Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable(onClick = { 
                        onNavigateTo(Screen.UploadHealthFile)
                    })
            ) {
                 Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = "Report Analysis",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Analyze your medical documentation",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                         imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

             Spacer(modifier = Modifier.height(16.dp))
             
             // Health Monitor
             Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable(onClick = { onNavigateTo(Screen.HealthMonitor) })
            ) {
                 Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF56AB2F), Color(0xFFA8E063))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = "Health Monitor",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Check your status of health",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                         imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
             Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


