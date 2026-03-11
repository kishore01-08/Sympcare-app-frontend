package com.simats.sympcareai

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditPatientProfileScreen(
    patientId: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var profile by remember { mutableStateOf<com.simats.sympcareai.data.response.PatientHealthProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    
    // Form States
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    
    val conditionsList = listOf("Diabetes", "Hypertension", "Asthma", "Heart Disease", "Thyroid", "Arthritis", "Dust Allergy")
    val selectedConditions = remember { mutableStateListOf<String>() }
    
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val scope = rememberCoroutineScope()

    // Fetch existing data
    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty()) {
            isLoading = true
            try {
                val request = mapOf("patient_id" to patientId)
                val response = com.simats.sympcareai.network.RetrofitClient.apiService.getHealthProfile(request)
                if (response.isSuccessful) {
                    profile = response.body()
                    profile?.let {
                        fullName = it.fullName ?: ""
                        age = it.age.toString()
                        gender = it.gender ?: ""
                        height = it.height.toString()
                        weight = it.weight.toString()
                        bloodGroup = it.bloodGroup
                        selectedConditions.clear()
                        val currentConditions = it.existingConditions.split(",").map { c -> c.trim() }.filter { c -> c.isNotEmpty() }
                        selectedConditions.addAll(currentConditions)
                    }
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Failed to load profile", android.widget.Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            profilePictureUri = uri
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF009688))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture Section
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePictureUri != null) {
                             Image(
                                painter = rememberAsyncImagePainter(profilePictureUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (profile?.profilePicture != null) {
                             val imageUrl = if (profile!!.profilePicture!!.startsWith("http")) profile!!.profilePicture else "http://10.0.2.2:8000${profile!!.profilePicture}"
                             Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF009688),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    
                    // Edit Overlay Icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .size(32.dp)
                            .background(Color(0xFF009688), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { }, // Read-only for now as backend serializer has it read_only
                    label = { Text("Full Name") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color(0xFFEEEEEE)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Age & Blood Group
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Blood Group") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Selection
                Text("Gender", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    listOf("Male", "Female", "Other").forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                            RadioButton(
                                selected = (gender == option),
                                onClick = { gender = option },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF009688))
                            )
                            Text(
                                text = option,
                                modifier = Modifier.clickable { gender = option }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Height & Weight
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Conditions
                Text("Health Conditions", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    conditionsList.forEach { condition ->
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
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        isSaving = true
                        
                        val map = HashMap<String, okhttp3.RequestBody>()
                        map["age"] = createPartFromString(age)
                        map["height"] = createPartFromString(height)
                        map["weight"] = createPartFromString(weight)
                        map["blood_group"] = createPartFromString(bloodGroup)
                        map["existing_conditions"] = createPartFromString(selectedConditions.joinToString(", "))
                        map["gender"] = createPartFromString(gender) 
                        map["patient_id"] = createPartFromString(patientId) 

                        var paramPart: MultipartBody.Part? = null

                        profilePictureUri?.let { uri ->
                            try {
                                val inputStream = contentResolver.openInputStream(uri)
                                val fileName = "profile_${System.currentTimeMillis()}.jpg"
                                val file = File(context.cacheDir, fileName)
                                val outputStream = FileOutputStream(file)

                                inputStream?.copyTo(outputStream)
                                inputStream?.close()
                                outputStream.close()

                                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                paramPart = MultipartBody.Part.createFormData(
                                    "profile_picture",
                                    file.name,
                                    requestFile
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        scope.launch {
                            try {
                                val response = com.simats.sympcareai.network.RetrofitClient.apiService.updateHealthProfile(map, paramPart)
                                if (response.isSuccessful) {
                                    android.widget.Toast.makeText(context, "Profile Updated", android.widget.Toast.LENGTH_SHORT).show()
                                    onSaveSuccess()
                                } else {
                                    android.widget.Toast.makeText(context, "Update Failed: ${response.code()}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            isSaving = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp)) else Text("Save Changes", fontSize = 16.sp)
                }
            }
        }
    }
}

fun createPartFromString(string: String): okhttp3.RequestBody {
    return string.toRequestBody(okhttp3.MultipartBody.FORM)
}
