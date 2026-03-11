package com.simats.sympcareai.data.response

import com.google.gson.annotations.SerializedName

// Using same DTO for response as request for now, or define specific response if needed
// For now, mirroring the structure used in ApiModels but checking if strict separation needed

data class PatientHealthProfileResponse(
    @SerializedName("patient_id") val patientId: String?,
    @SerializedName("full_name") val fullName: String?,
    val age: Int,
    val gender: String?,
    val height: Float,
    val weight: Float,
    @SerializedName("blood_group") val bloodGroup: String,
    @SerializedName("existing_conditions") val existingConditions: String = "",
    @SerializedName("profile_picture") val profilePicture: String? = null // URL
)

data class DoctorProfileResponse(
    @SerializedName("full_name") val fullName: String,
    val age: Int,
    val gender: String,
    val specialization: String,
    @SerializedName("profile_picture") val profilePicture: String? = null
)

data class MedicalReportResponse(
    @SerializedName("report_id") val id: Int?,
    @SerializedName("patient_id") val patientId: String?,
    @SerializedName("session_id") val sessionId: Int?,
    @SerializedName("file_url") val fileUrl: String?,
    val analysis: String?,
    @SerializedName("uploaded_at") val uploadedAt: String,
    val type: String? = null,
    val symptoms: List<String>? = null,
    val triage: Int? = null,
    @SerializedName("severity_score") val severityScore: Float? = null
)

data class AnalysisResultResponse(
    @SerializedName("session_id") val sessionId: Int,
    @SerializedName("file_url") val fileUrl: String,
    val analysis: String?,
    val error: String? = null
)

data class ChatSessionResponse(
    @SerializedName("session_id") val sessionId: Int,
    val disease: String,
    val triage: Int,
    val date: String
)

data class PatientOverviewResponse(
    @SerializedName("patient_name") val patientName: String,
    val phone: String,
    val email: String,
    val profile: PatientHealthProfileResponse?,
    val history: List<ChatSessionResponse>
)

data class SessionDetailResponse(
    val symptoms: List<String>?,
    @SerializedName("possible_diseases") val possibleDiseases: String?,
    val triage: Int,
    @SerializedName("severity_score") val severityScore: Float,
    val date: String,
    @SerializedName("ai_analysis") val aiAnalysis: String?,
    @SerializedName("file_url") val fileUrl: String?
)

data class SymptomsResponse(
    val symptoms: Map<String, List<String>>
)

data class QuestionsResponse(
    val questions: List<String>
)

data class AIAnalysisResponse(
    @SerializedName("main_disease") val mainDisease: String,
    @SerializedName("possible_diseases") val possibleDiseases: List<DiseaseRisk>,
    val triage: Int,
    @SerializedName("severity_score") val severityScore: Float
)

data class DiseaseRisk(
    val name: String,
    val probability: Float? = null
)

data class FileAnalysisResponse(
    val status: String,
    val report: String,
    @SerializedName("report_id") val reportId: Int? = null,
    @SerializedName("file_url") val fileUrl: String? = null,
    @SerializedName("uploaded_at") val uploadedAt: String? = null,
    val triage: Int? = null,
    @SerializedName("severity_score") val severityScore: Float? = null,
    @SerializedName("main_disease") val mainDisease: String? = null,
    @SerializedName("possible_diseases") val possibleDiseases: List<DiseaseRisk>? = null,
    val symptoms: List<String>? = null
)

data class FileUploadResponse(
    val status: String,
    @SerializedName("file_id") val fileId: Int,
    @SerializedName("file_url") val fileUrl: String? = null
)
data class ChatHistoryDTO(
    val user: String,
    val symptoms: List<String>,
    val answers: Map<String, String>,
    val disease: String,
    val triage: Int,
    @SerializedName("severity_score") val severityScore: Float,
    val date: String
)

data class ChatHistoryListResponse(
    val history: List<ChatHistoryDTO>
)

data class ViewedPatientResponse(
    @SerializedName("patient_id") val patientId: String,
    @SerializedName("patient_name") val patientName: String,
    @SerializedName("viewed_at") val viewedAt: String,
    val status: String,
    val symptoms: String?
)
