package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHealthProfileScreen(
    patientId: String,
    initialFullName: String = "",
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var fullName by remember { mutableStateOf(initialFullName) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") } // Default to Male
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("A+") }
    var expanded by remember { mutableStateOf(false) }

    // Custom Condition States
    var showCustomConditionDialog by remember { mutableStateOf(false) }
    var customConditionText by remember { mutableStateOf("") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Health Conditions
    val conditions = listOf("Diabetes", "Hypertension", "Asthma", "Heart Disease", "Thyroid", "Arthritis")
    val selectedConditions = remember { mutableStateListOf<String>() }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Custom Condition Dialog
    if (showCustomConditionDialog) {
        AlertDialog(
            onDismissRequest = { showCustomConditionDialog = false },
            title = { Text("Add Condition") },
            text = {
                OutlinedTextField(
                    value = customConditionText,
                    onValueChange = { customConditionText = it },
                    label = { Text("Condition Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (customConditionText.isNotBlank()) {
                        selectedConditions.add(customConditionText.trim())
                        customConditionText = ""
                        showCustomConditionDialog = false
                    }
                }) {
                    Text("Add", color = Color(0xFF009688))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomConditionDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    // Success Dialog Logic
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {}, // Prevent dismissal by clicking outside
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF009688),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Profile Created", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            text = {
                Text(
                    "Your health profile has been created successfully. Redirecting to login...",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {},
            dismissButton = {},
            containerColor = Color.White
        )
        LaunchedEffect(Unit) {
            delay(2000)
            showSuccessDialog = false
            onSaveClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .systemBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF009688), Color(0xFF00BCD4))
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Create Your Health Profile",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .offset(y = (-30).dp) // Overlap header
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Full Name (Optional binding if API supported it, but API request just has age/height/weight etc. 
                    // Actually checking ProfileRequests.kt: PatientHealthProfileRequest(patientId, age, height, weight, bloodGroup, existingConditions)
                    // Full Name is not in the request, probably stored in PatientUser model during signup.
                    // We can collect it but not send it, or assume it's just for UI.)
                    
                    Text("Full Name * (Display Only)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Age
                    Text("Age *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        trailingIcon = { Text("years", modifier = Modifier.padding(end = 16.dp), color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender
                    Text("Gender *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { gender = option }
                                    .padding(9.dp)
                            ) {
                                RadioButton(
                                    selected = (gender == option),
                                    onClick = { gender = option },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF009688))
                                )
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Height & Weight row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Height *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                trailingIcon = {
                                    Surface(color = Color(0xFF009688), shape = RoundedCornerShape(4.dp), modifier = Modifier.size(24.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("cm", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color.Black
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Weight *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                trailingIcon = {
                                    Surface(color = Color(0xFF009688), shape = RoundedCornerShape(4.dp), modifier = Modifier.size(24.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("kg", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Blood Group
                    Text("Blood Group *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            bloodGroups.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        bloodGroup = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Existing Health Conditions
                    Text("Existing Health Conditions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Select all that apply (optional)", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        conditions.forEach { condition ->
                            val isSelected = selectedConditions.contains(condition)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedConditions.remove(condition)
                                    else selectedConditions.add(condition)
                                },
                                label = { Text(condition) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF009688),
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                        // Custom Condition Chip
                        SuggestionChip(
                            onClick = { showCustomConditionDialog = true },
                            label = { Text("Add custom condition", color = Color(0xFF009688)) },
                            border = BorderStroke(1.dp, Color(0xFF009688)),
                            shape = RoundedCornerShape(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White)
                        )
                    }

                    if (selectedConditions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFFE0F2F1),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Selected: ${selectedConditions.size} conditions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF009688)
                                )
                                Text(
                                    selectedConditions.joinToString(", "),
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty() && bloodGroup.isNotEmpty()) {
                       kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                           try {
                               val request = com.simats.sympcareai.data.request.PatientHealthProfileRequest(
                                   patientId = patientId,
                                   age = age.toIntOrNull() ?: 0,
                                   gender = gender,
                                   height = height.toFloatOrNull() ?: 0f,
                                   weight = weight.toFloatOrNull() ?: 0f,
                                   bloodGroup = bloodGroup,
                                   existingConditions = selectedConditions.joinToString(", ")
                               )
                               
                               val response = com.simats.sympcareai.network.RetrofitClient.apiService.createHealthProfile(request)
                               
                               if (response.isSuccessful) {
                                   val body = response.body()
                                   if (body?.status == "profile_saved") {
                                       showSuccessDialog = true
                                   } else {
                                       android.widget.Toast.makeText(context, body?.error ?: "Failed to save profile", android.widget.Toast.LENGTH_SHORT).show()
                                   }
                               } else {
                                   android.widget.Toast.makeText(context, "Server Error: ${response.code()}", android.widget.Toast.LENGTH_SHORT).show()
                               }
                           } catch (e: Exception) {
                               android.util.Log.e("HealthProfile", "Error: ${e.message}", e)
                               android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                           }
                       }
                    } else {
                         android.widget.Toast.makeText(context, "Please fill all required fields", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)), // Teal
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Save & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}