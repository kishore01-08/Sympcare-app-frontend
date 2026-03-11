package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.sympcareai.data.response.SymptomsResponse
import com.simats.sympcareai.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SymptomSelectionScreen(
    onBackClick: () -> Unit,
    onContinueClick: (List<String>) -> Unit
) {
    // State to track symptoms from backend
    var categoricalSymptoms by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // State to track selected symptoms
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }

    // Fetch symptoms from backend
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = RetrofitClient.apiService.getSymptoms()
            if (response.isSuccessful) {
                categoricalSymptoms = response.body()?.symptoms ?: emptyMap()
            } else {
                errorMessage = "Failed to load symptoms: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Network error: ${e.message}"
        }
        isLoading = false
    }

    // Process symptoms for display (filter and flatten for search IF searching, otherwise keep categories)
    val displayData = remember(categoricalSymptoms, searchQuery) {
        if (searchQuery.isEmpty()) {
            categoricalSymptoms
        } else {
            categoricalSymptoms.mapValues { entry ->
                entry.value.filter { it.contains(searchQuery, ignoreCase = true) }
            }.filter { it.value.isNotEmpty() }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            // Bottom Bar with Search and Continue
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search for more symptoms...", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            disabledContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onContinueClick(selectedSymptoms) },
                        enabled = selectedSymptoms.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF009688),
                            disabledContainerColor = Color(0xFFBDBDBD)
                        )
                    ) {
                        Text(
                            text = "Continue to Chat",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                        )
                    )
                    .padding(start = 16.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
            ) {
                Column {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "What's bothering you?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Select all the symptoms you are experiencing so we can help you better.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }


            


            // Content Area
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF009688))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { 
                            isLoading = true
                            errorMessage = null
                        }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    displayData.forEach { (category, symptomsList) ->
                        item {
                            Text(
                                text = category,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F),
                                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                            )
                        }
                        
                        item {
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                symptomsList.forEach { symptom ->
                                    val isSelected = selectedSymptoms.contains(symptom)
                                    SymptomCard(
                                        symptom = symptom,
                                        isSelected = isSelected,
                                        modifier = Modifier.width(160.dp),
                                        onClick = {
                                            if (isSelected) {
                                                selectedSymptoms.remove(symptom)
                                            } else {
                                                selectedSymptoms.add(symptom)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SymptomCard(
    symptom: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF009688) else Color.Transparent
    val backgroundColor = if (isSelected) Color(0xFFE0F2F1) else Color.White
    val contentColor = if (isSelected) Color(0xFF009688) else Color.Black

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 // Placeholder for icon - could use a when statement to map symptom string to icon
                // For now just text
                Text(
                    text = symptom,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor,
                    textAlign = TextAlign.Center
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF009688),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}
