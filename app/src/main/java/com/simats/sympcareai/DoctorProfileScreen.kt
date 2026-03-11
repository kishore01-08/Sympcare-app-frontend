package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.data.response.DoctorProfileResponse
import com.simats.sympcareai.data.response.AccountInfoResponse
import com.simats.sympcareai.network.RetrofitClient
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DoctorProfileScreen(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    userId: String // Pass the doc_id from MainActivity
) {
    val context = LocalContext.current
    
    var fullName by remember { mutableStateOf("Loading...") }
    var age by remember { mutableStateOf<Int?>(null) }
    var gender by remember { mutableStateOf("Loading...") }
    var specialization by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch profile and account info on launch
    LaunchedEffect(Unit) {
        val docIdMap = mapOf("doc_id" to userId)
        
        try {
            // 1. Fetch Profile Details
            val profileResponse = RetrofitClient.apiService.getDoctorProfile(docIdMap)
            if (profileResponse.isSuccessful) {
                val profile = profileResponse.body()
                fullName = profile?.fullName ?: fullName
                age = profile?.age
                gender = profile?.gender ?: gender
                specialization = profile?.specialization ?: specialization
            } else {
                Toast.makeText(context, "Profile separation failed", Toast.LENGTH_SHORT).show()
            }

            // 2. Fetch Account Info (for Email)
            val accountResponse = RetrofitClient.apiService.getDoctorAccountInfo(docIdMap)
            isLoading = false
            if (accountResponse.isSuccessful) {
                email = accountResponse.body()?.email ?: email
            } else {
                Toast.makeText(context, "Account Info Error", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            isLoading = false
            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Height to accommodate the curve and avatar
            ) {
                // Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6A5ACD), Color(0xFF9683EC))
                            )
                        )
                ) {
                    // Toolbar Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
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

                        IconButton(
                            onClick = onEditClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }

                    // Title
                    Text(
                        text = "Doctor Profile",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 20.dp)
                    )
                }

                // Profile Avatar
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomCenter)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Color(0xFF6A5ACD))
                        } else {
                            val initials = fullName.split(" ").filter { it.isNotEmpty() }.take(2).map { it[0] }.joinToString("").uppercase()
                            Text(
                                text = initials,
                                color = Color(0xFF6A5ACD),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Personal Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3436)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        ProfileInfoItem(
                            icon = Icons.Outlined.Person,
                            label = "Full Name",
                            value = fullName
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        ProfileInfoItem(
                            icon = Icons.Outlined.CalendarToday,
                            label = "Age",
                            value = if (age != null) "$age years" else "Not set"
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        ProfileInfoItem(
                            icon = Icons.Outlined.People,
                            label = "Gender",
                            value = gender
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        ProfileInfoItem(
                            icon = Icons.Outlined.MedicalServices,
                            label = "Specialization",
                            value = specialization
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // System Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "System Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3436)
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        ProfileInfoItem(
                            icon = Icons.Outlined.Email,
                            label = "Email Address",
                            value = email,
                            isProtected = true
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))

                        ProfileInfoItem(
                            icon = Icons.Outlined.Badge,
                            label = "Doctor ID",
                            value = userId,
                            isProtected = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    isProtected: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF95A5A6),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF95A5A6)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color(0xFF2D3436),
                fontWeight = FontWeight.SemiBold
            )
        }
        if (isProtected) {
            Surface(
                color = Color(0xFFE6E6FA), // Lavender
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Protected",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF6A5ACD) // SlateBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Protected",
                        color = Color(0xFF6A5ACD), // SlateBlue
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
