package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatientProfileScreen(
    patientId: String, // Received from MainActivity
    refreshTrigger: Int = 0,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit
) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf<com.simats.sympcareai.data.response.PatientHealthProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch profile
    LaunchedEffect(patientId, refreshTrigger) {
        if (patientId.isNotEmpty()) {
            isLoading = true
            try {
                val request = mapOf("patient_id" to patientId, "t" to System.currentTimeMillis().toString())
                val response = com.simats.sympcareai.network.RetrofitClient.apiService.getHealthProfile(request)
                if (response.isSuccessful) {
                    profile = response.body()
                } else {
                    error = "Failed to load profile"
                    android.widget.Toast.makeText(context, "Error: ${response.code()}", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Failed to load profile: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
            isLoading = false
        } else {
            isLoading = false // No ID, can't fetch
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = Screen.PatientHome, // heuristic, serves as profile tab context
                onNavigateTo = onNavigateTo
            )
        }
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
                    .height(240.dp)
            ) {
                // Background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFF009688))
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        "My Profile",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 28.dp)
                    )
                }

                // Profile Card
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color(0xFF009688), modifier = Modifier.size(30.dp))
                            } else if (profile?.profilePicture != null) {
                                val imageUrl = if (profile!!.profilePicture!!.startsWith("http"))
                                    profile!!.profilePicture
                                else
                                    "http://10.0.2.2:8000${profile!!.profilePicture}"

                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFE0F2F1),
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.padding(16.dp),
                                        tint = Color(0xFF009688)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isLoading) "Loading..." else profile?.fullName ?: "Guest User", 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isLoading) "ID: Loading..." else "Patient ID: ${profile?.patientId ?: patientId}", 
                            fontSize = 14.sp, 
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Info
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF009688))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(label = "Age", value = if (isLoading) "--" else "${profile?.age ?: "--"} yrs", modifier = Modifier.weight(1f))
                    InfoCard(label = "Blood Group", value = if (isLoading) "--" else profile?.bloodGroup ?: "--", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(label = "Height", value = if (isLoading) "--" else "${profile?.height ?: "--"} cm", modifier = Modifier.weight(1f))
                    InfoCard(label = "Weight", value = if (isLoading) "--" else "${profile?.weight ?: "--"} kg", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(label = "Gender", value = if (isLoading) "--" else profile?.gender ?: "--", modifier = Modifier.weight(1f))
                     // Filler to balance row
                    Spacer(modifier = Modifier.weight(1f)) 
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Medical Conditions
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Medical Conditions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isLoading) {
                    Text("Loading conditions...", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                } else {
                    val conditions = profile?.existingConditions?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
                    
                    if (conditions.isEmpty()) {
                        Text("No existing conditions", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            conditions.forEach { condition ->
                                Surface(
                                    color = Color(0xFFE0F2F1),
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF009688))
                                ) {
                                    Text(
                                        text = condition,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        color = Color(0xFF00796B),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
