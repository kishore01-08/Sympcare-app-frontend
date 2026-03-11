package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorSettingsScreen(
    onNavigateTo: (Screen) -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
             AppBottomNavigationBar(
                currentScreen = Screen.DoctorSettings,
                onNavigateTo = onNavigateTo,
                isDoctor = true
            )
        },
        containerColor = Color(0xFFF5F5F5)
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
                    .height(180.dp)
            ) {
                 // Gradient Background (Doctor Theme - Purple)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Settings Icon on Right
                        IconButton(
                            onClick = { },
                             colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                    
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 10.dp)
                    )
                }
            }

            // Menu Items List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-20).dp) // Slight overlap
            ) {
                // Group 1
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Outlined.Security,
                            title = "Data & Privacy",
                            subtitle = "Manage permissions and data security",
                            iconBgColor = Color(0xFFE0F2F1), // Fallback
                            iconColor = Color(0xFF009688),
                            onClick = { onNavigateTo(Screen.DataAndPrivacy) },
                            backgroundGradient = listOf(Color(0xFF009688), Color(0xFF00BFA5))
                        )
                        Divider(color = Color.LightGray.copy(alpha = 0.2f))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Description,
                            title = "Terms of Service",
                            subtitle = "View app usage policies",
                            iconBgColor = Color(0xFFE3F2FD), // Fallback
                            iconColor = Color(0xFF2196F3),
                            onClick = { onNavigateTo(Screen.TermsOfService) },
                            backgroundGradient = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                        )
                         Divider(color = Color.LightGray.copy(alpha = 0.2f))
                        SettingsMenuItem(
                            icon = Icons.Outlined.Person,
                            title = "Account",
                            subtitle = "Manage doctor profile, password",
                            iconBgColor = Color(0xFFF3E5F5), // Fallback
                            iconColor = Color(0xFF9C27B0),
                            onClick = { onNavigateTo(Screen.DoctorAccountSettings) },
                            backgroundGradient = listOf(Color(0xFF9C27B0), Color(0xFFBA68C8))
                        )


                         SettingsMenuItem(
                            icon = Icons.Outlined.Info,
                            title = "About App",
                            subtitle = "Version, developer info, app details",
                            iconBgColor = Color(0xFFE0F7FA), // Fallback
                            iconColor = Color(0xFF00BCD4),
                            onClick = { onNavigateTo(Screen.AboutApp) },
                            backgroundGradient = listOf(Color(0xFF00BCD4), Color(0xFF4DD0E1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Logout Button
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = "Log Out", tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
