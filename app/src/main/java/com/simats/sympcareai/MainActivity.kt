package com.simats.sympcareai


import android.os.Bundle
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.simats.sympcareai.ui.theme.SympcareAITheme
import androidx.compose.ui.graphics.Color
import com.simats.sympcareai.data.response.AIAnalysisResponse
import com.simats.sympcareai.data.response.ChatHistoryDTO
import com.simats.sympcareai.network.RetrofitClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            // Force Light Theme as per user request to remove Dark Mode feature
            SympcareAITheme(darkTheme = false) {
                // Manual BackStack Implementation
                val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
                var selectedSymptoms by remember { mutableStateOf(emptyList<String>()) }
                
                // Active Patients List (Hoisted State)
                val patients = remember { mutableStateListOf<DoctorPatient>() }
                
                var patientListFilter by remember { mutableStateOf("Total") } // "Total", "Pending", "Completed"
                
                // Temporary state for Patient ID flow (SignUp -> Disclaimer -> Profile)
                var tempPatientName by remember { mutableStateOf("") }
                var tempPatientId by remember { mutableStateOf("") }
                var tempDoctorName by remember { mutableStateOf("") }
                var tempDoctorId by remember { mutableStateOf("") }
                var profileRefreshTrigger by remember { mutableStateOf(0) }
                var resetEmail by remember { mutableStateOf("") }
                var doctorResetEmail by remember { mutableStateOf("") }
                // Derived counts
                val patientsTodayCount = patients.count { it.status != "Completed" }
                val completedPatientsCount = patients.count { it.status == "Completed" }

                var doctorSymptomList by remember { mutableStateOf(emptyList<String>()) }
                var doctorPatientTargetId by remember { mutableStateOf("") }



                // --- Pending File Analysis States (For Unified Report) ---
                var pendingFileUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
                var pendingFileDescription by remember { mutableStateOf("") }
                var pendingFileCategory by remember { mutableStateOf("General") }
                var fileAnalysisUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
                var fileAnalysisDescription by remember { mutableStateOf("") }
                var fileAnalysisCategory by remember { mutableStateOf("General") }
                var fileAnalysisSessionId by remember { mutableStateOf(-1) }

                val viewedPatientIds = remember { mutableStateListOf<String>() }
                val currentScreen = backStack.lastOrNull() ?: Screen.Splash

                // Global BackHandler
                // Enabled only whenever there's more than 1 screen in the stack
                BackHandler(enabled = backStack.size > 1) {
                    backStack.removeLast()
                }

                // Helper to navigate forward
                fun navigateTo(screen: Screen) {
                    backStack.add(screen)
                }
//testinhhh
                // Helper to replace current screen (e.g. Splash -> Intro)
                fun replaceWith(screen: Screen) {
                    if (backStack.isNotEmpty()) {
                        backStack.removeLast()
                    }
                    backStack.add(screen)
                }
                
                // Helper to reset to a specific screen (clearing stack)
                fun resetTo(screen: Screen) {
                    backStack.clear()
                    backStack.add(screen)
                }

                fun navigateBack() {
                    if (backStack.size > 1) {
                        backStack.removeLast()
                    }
                }

                fun fetchViewedPatients(doctorId: String) {
                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.apiService.listViewedPatients(mapOf("doc_id" to doctorId))
                            if (response.isSuccessful) {
                                val savedPatients = response.body() ?: emptyList()
                                patients.clear()
                                patients.addAll(savedPatients.map { vp ->
                                    DoctorPatient(
                                        id = vp.patientId,
                                        name = vp.patientName,
                                        //symptom = vp.symptoms ?: "No records",
                                        time = "Viewed",
                                        status = if (vp.status == "Completed") "Completed" else "Online",
                                        statusColor = if (vp.status == "Completed") Color(0xFFE8EAF6) else Color(0xFFE0F2F1),
                                        statusTextColor = if (vp.status == "Completed") Color(0xFF3F51B5) else Color(0xFF009688),
                                        initial = vp.patientName.take(1).uppercase(),
                                        initialBg = Color(0xFF00BFA5)
                                    )
                                })
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                fun saveViewedPatient(doctorId: String, patientId: String, patientName: String, status: String = "viewed", symptoms: String? = null) {
                    lifecycleScope.launch {
                        try {
                            val params = mutableMapOf(
                                "doc_id" to doctorId,
                                "patient_id" to patientId,
                                "patient_name" to patientName,
                                "status" to status
                            )
                            if (symptoms != null) params["symptoms"] = symptoms
                            
                            RetrofitClient.apiService.saveViewedPatient(params)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                var collectedAnswers by remember { mutableStateOf(emptyMap<String, String>()) }
                
                when (currentScreen) {
                    Screen.Splash -> {
                        // Splash -> Login (Directly, bypassing Intro)
                        SplashScreen(onTimeout = { replaceWith(Screen.Login) })
                    }

                    Screen.Login -> {
                        PermissionRequestLogic() // Request permissions on Login screen entry
                        LoginScreen(
                            onDoctorSignUpClick = { navigateTo(Screen.DoctorSignUp) },
                            onDoctorForgotPasswordClick = { navigateTo(Screen.DoctorForgotPassword) },
                            onDoctorLoginSuccess = { id, name -> 
                                tempDoctorId = id
                                tempDoctorName = name
                                fetchViewedPatients(id)
                                resetTo(Screen.DoctorHome) 
                            },
                            onPatientSignUpClick = { navigateTo(Screen.PatientSignUp) },
                            onPatientForgotPasswordClick = { navigateTo(Screen.PatientForgotPassword) },
                            onPatientLoginSuccess = { id, name ->
                                tempPatientId = id
                                tempPatientName = name
                                resetTo(Screen.PatientHome) 
                            },
                            onPatientProfileIncomplete = { id, name ->
                                android.widget.Toast.makeText(context, "Patient found ! Redirecting to health profile...", android.widget.Toast.LENGTH_SHORT).show()
                                tempPatientId = id
                                tempPatientName = name
                                navigateTo(Screen.PatientHealthProfile)
                            },
                            initialPage = 0 // Default to Patient
                        )
                    }
                    Screen.DoctorHome -> {
                        DoctorHomeScreen(
                            onPatientPortalClick = { navigateTo(Screen.DoctorPatientPortal) },
                            onNavigateTo = { screen -> 
                                if (screen == Screen.DoctorPatients) patientListFilter = "Total"
                                navigateTo(screen) 
                            },
                            onProfileClick = { navigateTo(Screen.DoctorProfile) },
                            onPatientsTodayClick = {
                                patientListFilter = "Pending"
                                navigateTo(Screen.DoctorPatients)
                            },
                            onCompletedPatientsClick = {
                                patientListFilter = "Completed"
                                navigateTo(Screen.DoctorPatients)
                            },
                            doctorName = tempDoctorName,
                            patientsTodayCount = patientsTodayCount,
                            completedPatientsCount = completedPatientsCount,
                            patients = patients
                        )
                    }
                    Screen.DoctorProfile -> {
                        DoctorProfileScreen(
                            onBackClick = { navigateBack() },
                            onEditClick = { navigateTo(Screen.EditDoctorProfile) },
                            userId = tempDoctorId
                        )
                    }
                    Screen.EditDoctorProfile -> {
                        EditDoctorProfileScreen(
                            onBackClick = { navigateBack() },
                            doctorId = tempDoctorId
                        )
                    }
                    Screen.DoctorPatients -> {
                        DoctorPatientsScreen(
                            onNavigateTo = { screen ->
                                if (screen == Screen.DoctorPatients) patientListFilter = "Total"
                                navigateTo(screen)
                            },
                            onBackClick = { navigateBack() },
                            onPatientComplete = { patient ->
                                // Update status to Completed
                                val index = patients.indexOfFirst { it.id == patient.id }
                                if (index != -1) {
                                    patients[index] = patients[index].copy(
                                        status = "Completed",
                                        statusColor = Color(0xFFE8EAF6),
                                        statusTextColor = Color(0xFF3F51B5)
                                    )
                                    // Persistent update ⭐
                                    saveViewedPatient(tempDoctorId, patient.id, patient.name, "Completed")
                                }
                            },
                            patients = patients,
                            filter = patientListFilter
                        )
                    }
                    Screen.DoctorPatientPortal -> {
                        DoctorPatientPortalScreen(
                            onBackClick = { navigateBack() },
                             onViewPatientDetails = { patientId ->
                                if (patientId.isNotEmpty()) {
                                    val existingIndex = patients.indexOfFirst { it.id == patientId }
                                    if (existingIndex != -1) {
                                        // Move to top if already exists
                                        val patient = patients.removeAt(existingIndex)
                                        patients.add(0, patient)
                                    } else {
                                        // Add new patient to top
                                        patients.add(0, DoctorPatient(
                                            id = patientId,
                                            name = "Patient $patientId",
                                            time = "Now",
                                            status = "Online",
                                            statusColor = Color(0xFFE0F2F1),
                                            statusTextColor = Color(0xFF009688),
                                            initial = patientId.take(1).uppercase(),
                                            initialBg = Color(0xFF00BFA5)
                                        ))
                                    }
                                    
                                    // Save to backend
                                    val currentPatient = patients.firstOrNull { it.id == patientId }
                                    if (currentPatient != null) {
                                        saveViewedPatient(tempDoctorId, patientId, currentPatient.name)
                                    }
                                }
                                navigateTo(Screen.DoctorPatientDetails(patientId))
                            },
                        )
                    }
                    Screen.CreatePatient -> {
                        CreatePatientScreen(
                            onBackClick = { navigateBack() },
                            onSubmitAndAnalyze = { id, symptoms ->
                                doctorPatientTargetId = id
                                doctorSymptomList = symptoms.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                navigateTo(Screen.AIPatientAnalysis)
                            }
                        )
                    }
                    Screen.AIPatientAnalysis -> {
                        AIPatientAnalysisScreen(
                            onBackClick = { navigateBack() },
                            patientId = doctorPatientTargetId,
                            symptoms = doctorSymptomList
                        )
                    }
                    is Screen.DoctorPatientDetails -> {
                        DoctorPatientDetailsScreen(
                            patientId = currentScreen.patientId,
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            onDataLoaded = { name, ->
                                val index = patients.indexOfFirst { it.id == currentScreen.patientId }
                                if (index != -1) {
                                    patients[index] = patients[index].copy(
                                        name = name,
                                        initial = name.take(1).uppercase()
                                    )
                                    // Update persistence with real name and symptom ⭐
                                    saveViewedPatient(tempDoctorId, currentScreen.patientId, name)
                                }
                            }
                        )
                    }
// Removed PatientLogin route as it's merged into Login

                    Screen.DoctorSignUp -> {
                        DoctorSignUpScreen(
                            onSignInClick = { navigateBack() }, 
                            onSignUpSuccess = { id, name -> 
                                tempDoctorId = id
                                tempDoctorName = name
                                navigateTo(Screen.DoctorDisclaimer) 
                            }
                        )
                    }

                    Screen.PatientSignUp -> {
                        PatientSignUpScreen(
                            onSignInClick = { navigateBack() },
                            onSignUpSuccess = { id, name -> 
                                tempPatientId = id
                                tempPatientName = name
                                navigateTo(Screen.PatientDisclaimer) 
                            }
                        )
                    }
                    Screen.DoctorForgotPassword -> {
                        DoctorForgotPasswordScreen(
                            onCancelClick = { navigateBack() },
                            onSendOtpClick = { email ->
                                doctorResetEmail = email
                                navigateTo(Screen.DoctorResetPassword)
                            }
                        )
                    }
                    Screen.DoctorResetPassword -> {
                        DoctorResetPasswordScreen(
                            email = doctorResetEmail,
                            onBackClick = { navigateBack() },
                            onSaveClick = { resetTo(Screen.Login) }
                        )
                    }
                    Screen.PatientForgotPassword -> {
                        PatientForgotPasswordScreen(
                            onCancelClick = { navigateBack() },
                            onSendOtpClick = { email ->
                                resetEmail = email
                                navigateTo(Screen.PatientResetPassword)
                            }
                        )
                    }
                    Screen.PatientResetPassword -> {
                        PatientResetPasswordScreen(
                            email = resetEmail,
                            onBackClick = { navigateBack() },
                            onSaveClick = { resetTo(Screen.Login) }
                        )
                    }
                    Screen.DoctorDisclaimer -> {
                        DisclaimerScreen(
                            onAcceptClick = { navigateTo(Screen.DoctorProfileRegistration) }, // Flow: Disclaimer -> Doctor Profile Registration
                            isDoctor = true
                        )
                    }
                    Screen.DoctorProfileRegistration -> {
                        DoctorProfileRegistrationScreen(
                            doctorId = tempDoctorId,
                            initialFullName = tempDoctorName,
                            onBackClick = { navigateBack() },
                            onSaveSuccess = { resetTo(Screen.Login) }
                        )
                    }
                    Screen.PatientDisclaimer -> {
                        DisclaimerScreen(
                            onAcceptClick = { navigateTo(Screen.PatientHealthProfile) }, // Flow: Disclaimer -> Health Profile
                            isDoctor = false
                        )
                    }
                    Screen.PatientHealthProfile -> {
                        PatientHealthProfileScreen(
                            patientId = tempPatientId,
                            initialFullName = tempPatientName,
                            onBackClick = { navigateBack() },
                            onSaveClick = { resetTo(Screen.Login) }
                        )
                    }
                    Screen.PatientHome -> {
                        PatientHomeScreen(
                            patientName = tempPatientName,
                            patientId = tempPatientId,
                            currentSessionId = fileAnalysisSessionId,
                            onChatClick = { 
                                // Reset symptoms and answers before starting new chat
                                selectedSymptoms = emptyList()
                                collectedAnswers = emptyMap()
                                navigateTo(Screen.SymptomSelection) 
                            },
                            onVoiceClick = { 
                                selectedSymptoms = emptyList()
                                collectedAnswers = emptyMap()
                                navigateTo(Screen.VoiceAssistant) 
                            },
                            onHealthReportClick = { navigateTo(Screen.HealthReports()) },
                            onProfileClick = { navigateTo(Screen.PatientProfile) },
                            onNavigateTo = { screen ->
                                if (screen is Screen.UploadHealthFileWithSession) {
                                    fileAnalysisSessionId = screen.sessionId
                                    navigateTo(Screen.UploadHealthFile)
                                } else {
                                    navigateTo(screen)
                                }
                            },
                            onSessionCreated = { sessionId ->
                                fileAnalysisSessionId = sessionId
                            }
                        )
                    }
                    Screen.Chat -> {
                        ChatScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            onFinishClick = { analysisResult ->
                                navigateTo(Screen.SymptomAnalysisResult(analysisResult, selectedSymptoms))
                            },
                            selectedSymptoms = selectedSymptoms,
                            patientId = tempPatientId,
                            sessionId = fileAnalysisSessionId
                        )
                    }
                    is Screen.SymptomAnalysisResult -> {
                        SymptomAnalysisResultScreen(
                            onBackClick = { resetTo(Screen.PatientHome) },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            analysisResult = currentScreen.result,
                            selectedSymptoms = currentScreen.symptoms
                        )
                    }
                    is Screen.SymptomReportDetails -> {
                        SymptomReportDetailsScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            symptoms = currentScreen.symptoms,
                            disease = currentScreen.disease,
                            date = currentScreen.date,
                            severityScore = currentScreen.severityScore,
                            triage = currentScreen.triage
                        )
                    }
                    Screen.VoiceAssistant -> {
                        VoiceAssistantScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            onAnalyseClick = { analysisResult ->
                                navigateTo(Screen.SymptomAnalysisResult(analysisResult, selectedSymptoms))
                            },
                            selectedSymptoms = selectedSymptoms,
                            patientId = tempPatientId,
                            sessionId = fileAnalysisSessionId
                        )
                    }
                    Screen.DoctorSettings -> {
                        DoctorSettingsScreen(
                            onNavigateTo = { screen -> navigateTo(screen) },
                            onBackClick = { navigateBack() },
                            onLogoutClick = { resetTo(Screen.Login) }
                        )
                    }
                    Screen.PatientSettings -> {
                        PatientSettingsScreen(
                            onNavigateTo = { screen -> navigateTo(screen) },
                            onBackClick = { navigateBack() },
                            onLogoutClick = { resetTo(Screen.Login) }
                        )
                    }
                    Screen.UploadHealthFile -> {
                        UploadHealthFileScreen(
                            patientId = tempPatientId,
                            symptoms = selectedSymptoms,
                            answers = collectedAnswers,
                            onBackClick = { navigateBack() },
                            onAnalyseClick = { uris, desc, cat ->
                                navigateTo(Screen.MedicalReportResult(
                                    reportId = fileAnalysisSessionId,
                                    uris = uris,
                                    description = desc,
                                    category = cat
                                ))
                            },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            initialFileUris = pendingFileUris,
                            initialDescription = pendingFileDescription,
                            initialCategory = pendingFileCategory
                        )
                    }
                    is Screen.ReportAnalysis -> {
                        ReportAnalysisScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            reportId = currentScreen.reportId
                        )
                    }
                    is Screen.MedicalReportResult -> {
                        MedicalReportResultScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> resetTo(screen) },
                            patientId = tempPatientId,
                            uris = currentScreen.uris,
                            description = currentScreen.description,
                            category = currentScreen.category,
                            reportId = currentScreen.reportId
                        )
                    }
                    Screen.Feedback -> {
                        FeedbackScreen(
                            onBackClick = { navigateBack() },
                            onSubmitClick = { navigateTo(Screen.PatientHome) }, // Submit -> Home
                            onNavigateTo = { screen -> navigateTo(screen) }
                        )
                    }
                    is Screen.HealthReports -> {
                        val color = currentScreen.themeColor ?: Color(0xFFEF6C00)
                        HealthReportsScreen(
                            patientId = if (currentScreen.patientId.isNotEmpty()) currentScreen.patientId else tempPatientId,
                            initialTab = currentScreen.initialTab,
                            themeColor = color,
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) }
                        )
                    }
                    is Screen.SavedReport -> {
                        SavedReportScreen(
                            reportId = currentScreen.reportId,
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) }
                        )
                    }
                    Screen.PatientProfile -> {
                        PatientProfileScreen(
                            patientId = tempPatientId,
                            refreshTrigger = profileRefreshTrigger,
                            onBackClick = { navigateBack() },
                            onEditClick = { navigateTo(Screen.EditPatientProfile) },
                            onNavigateTo = { screen -> navigateTo(screen) }
                        )
                    }
                    Screen.EditPatientProfile -> {
                        com.simats.sympcareai.EditPatientProfileScreen(
                            patientId = tempPatientId,
                            onBackClick = { navigateBack() },
                            onSaveSuccess = { 
                                profileRefreshTrigger++
                                navigateBack() 
                            }
                        )
                    }


                    Screen.ChatHistory -> {
                        ChatHistoryScreen(
                            onBackClick = { navigateBack() },
                            onChatClick = { historyItem -> navigateTo(Screen.ChatReadOnly(historyItem)) },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            patientId = tempPatientId
                        )
                    }
                    is Screen.ChatReadOnly -> {
                        ChatScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            onFinishClick = {}, // Hide finish button if needed or handle logic
                            isReadOnly = true,
                            patientId = tempPatientId,
                            history = currentScreen.history
                        )
                    }
                    Screen.HealthMonitor -> {
                        HealthAssessmentScreen(
                            onBackClick = { resetTo(Screen.PatientHome) },
                            onComplete = { score ->
                                navigateTo(Screen.HealthAnalysisResult(score))
                            }
                        )
                    }
                    is Screen.HealthAssessment -> {
                        HealthAssessmentScreen(
                            onBackClick = { navigateBack() },
                            onComplete = { score ->
                                navigateTo(Screen.HealthAnalysisResult(score))
                            }
                        )
                    }
                    is Screen.HealthAnalysisResult -> {
                        HealthAnalysisResultScreen(
                            score = currentScreen.score,
                            onCloseClick = { resetTo(Screen.PatientHome) },
                            onStartNewSessionClick = { 
                                navigateTo(Screen.HealthMonitor) 
                            }
                        )
                    }
                    Screen.DoctorChat -> {
                        DoctorChatScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) }
                        )
                    }
                    Screen.SymptomSelection -> {
                        SymptomSelectionScreen(
                            onBackClick = { navigateBack() },
                            onContinueClick = { symptoms -> 
                                selectedSymptoms = symptoms
                                navigateTo(Screen.Chat) 
                            }
                        )
                    }
                    Screen.VoiceSymptomSelection -> {
                        SymptomSelectionScreen(
                            onBackClick = { navigateBack() },
                            onContinueClick = { symptoms ->
                                selectedSymptoms = symptoms
                                navigateTo(Screen.VoiceAssistant)
                            }
                        )
                    }
                    Screen.AccountSettings -> {
                        // Default fallback, though should use specific routes
                        AccountSettingsScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            isDoctor = false, // Default
                            userId = tempPatientId
                        )
                    }
                    Screen.PatientAccountSettings -> {
                        AccountSettingsScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            isDoctor = false,
                            userId = tempPatientId
                        )
                    }
                    Screen.DoctorAccountSettings -> {
                        AccountSettingsScreen(
                            onBackClick = { navigateBack() },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            isDoctor = true,
                            userId = tempDoctorId
                        )
                    }
                    Screen.ResetPassword -> {
                         // Fallback or handle based on context if possible, but better to use specific routes
                        ResetPasswordScreen(
                            userId = tempPatientId,
                            onBackClick = { navigateBack() },
                            onSuccess = { navigateBack() },
                            isDoctor = false 
                        )
                    }
                    Screen.PatientResetPasswordLoggedIn -> {
                        ResetPasswordScreen(
                            userId = tempPatientId,
                            onBackClick = { navigateBack() },
                            onSuccess = { navigateBack() },
                            isDoctor = false
                        )
                    }
                    Screen.DoctorResetPasswordLoggedIn -> {
                        ResetPasswordScreen(
                            userId = tempDoctorId,
                            onBackClick = { navigateBack() },
                            onSuccess = { navigateBack() },
                            isDoctor = true
                        )
                    }
                    Screen.AboutApp -> {
                        AboutAppScreen(onBackClick = { navigateBack() })
                    }
                    Screen.TermsOfService -> {
                        TermsAndConditionsScreen(onBackClick = { navigateBack() })
                    }
                    Screen.DataAndPrivacy -> {
                        DataPrivacyScreen(onBackClick = { navigateBack() })
                    }
                    is Screen.UploadHealthFileWithReport -> {
                        UploadHealthFileScreen(
                            patientId = tempPatientId,
                            symptoms = selectedSymptoms,
                            answers = collectedAnswers,
                            onBackClick = { navigateBack() },
                            onAnalyseClick = { uris, desc, cat ->
                                navigateTo(Screen.MedicalReportResult(
                                    reportId = currentScreen.reportId,
                                    uris = uris,
                                    description = desc,
                                    category = cat
                                ))
                            },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            initialFileUris = pendingFileUris,
                            initialDescription = pendingFileDescription,
                            initialCategory = pendingFileCategory
                        )
                    }
                    is Screen.UploadHealthFileWithSession -> {
                        // This case is usually handled in onNavigateTo redirection,
                        // but added here for exhaustive when block.
                        fileAnalysisSessionId = (currentScreen as Screen.UploadHealthFileWithSession).sessionId
                        UploadHealthFileScreen(
                            patientId = tempPatientId,
                            symptoms = selectedSymptoms,
                            answers = collectedAnswers,
                            onBackClick = { navigateBack() },
                            onAnalyseClick = { uris, desc, cat ->
                                navigateTo(Screen.MedicalReportResult(
                                    reportId = fileAnalysisSessionId,
                                    uris = uris,
                                    description = desc,
                                    category = cat
                                ))
                            },
                            onNavigateTo = { screen -> navigateTo(screen) },
                            initialFileUris = pendingFileUris,
                            initialDescription = pendingFileDescription,
                            initialCategory = pendingFileCategory
                        )
                    }
                    Screen.DoctorProfileRegistration -> {
                        // Add implementation if needed, or placeholder
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object DoctorSignUp : Screen()
    object PatientSignUp : Screen()
    object DoctorForgotPassword : Screen()
    object PatientForgotPassword : Screen()
    object PatientResetPassword : Screen()
    object DoctorResetPassword : Screen()
    object DoctorDisclaimer : Screen()
    object PatientDisclaimer : Screen()
    object PatientHealthProfile : Screen()
    object PatientHome : Screen()
    object DoctorHome : Screen()
    object DoctorProfile : Screen()
    object EditDoctorProfile : Screen()
    object DoctorPatients : Screen()
    object DoctorPatientPortal : Screen()
    object CreatePatient : Screen()
    object AIPatientAnalysis : Screen()
    data class DoctorPatientDetails(val patientId: String) : Screen()
    object Chat : Screen()
    object VoiceAssistant : Screen()
    object DoctorSettings : Screen()
    object PatientSettings : Screen()
    object DoctorChat : Screen()
    object UploadHealthFile : Screen()
    data class ReportAnalysis(val reportId: Int = -1) : Screen()
    data class MedicalReportResult(
        val reportId: Int = -1,
        val uris: List<Uri> = emptyList(),
        val description: String = "",
        val category: String = ""
    ) : Screen()
    object Feedback : Screen()
    data class HealthReports(val patientId: String = "", val initialTab: Int = 0, val themeColor: Color? = null) : Screen()
    data class SavedReport(val reportId: Int) : Screen()
    object PatientProfile : Screen()
    object EditPatientProfile : Screen()
    object ChatHistory : Screen()
    data class ChatReadOnly(val history: com.simats.sympcareai.data.response.ChatHistoryDTO) : Screen()
    object HealthMonitor : Screen()
    data class HealthAssessment(val type: HealthAssessmentType = HealthAssessmentType.MORNING_WELLNESS) : Screen()
    data class HealthAnalysisResult(val score: Int = 0) : Screen()
    object SymptomSelection : Screen()
    object VoiceSymptomSelection : Screen()
    object AccountSettings : Screen()
    object ResetPassword : Screen()
    object PatientAccountSettings : Screen()
    object DoctorAccountSettings : Screen()
    object PatientResetPasswordLoggedIn : Screen()
    object DoctorResetPasswordLoggedIn : Screen()
    object AboutApp : Screen()
    object TermsOfService : Screen()
    object DataAndPrivacy : Screen()
    object DoctorProfileRegistration : Screen()
    
    data class SymptomAnalysisResult(
        val result: AIAnalysisResponse,
        val symptoms: List<String>
    ) : Screen()
    
    data class SymptomReportDetails(
        val symptoms: List<String>,
        val disease: String,
        val date: String,
        val severityScore: Float,
        val triage: Int? = null
    ) : Screen()
    
    // Parameterized screens
    data class UploadHealthFileWithReport(val reportId: Int) : Screen()
    data class UploadHealthFileWithSession(val sessionId: Int) : Screen()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SympcareAITheme {
        Greeting("Android")
    }
}
