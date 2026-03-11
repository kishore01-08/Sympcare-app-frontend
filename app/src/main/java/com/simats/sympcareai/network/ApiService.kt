package com.simats.sympcareai.network

import com.simats.sympcareai.data.request.*
import com.simats.sympcareai.data.response.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    // --- Patient Auth ---
    @POST("patient/register/")
    suspend fun registerPatient(@Body request: PatientRegisterRequest): Response<AuthResponse>

    @POST("patient/verify-otp/")
    suspend fun verifyPatientOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("patient/login/")
    suspend fun loginPatient(@Body request: PatientLoginRequest): Response<AuthResponse>

    @POST("patient/forgot/send-otp/")
    suspend fun forgotPasswordSendOtp(@Body request: ForgotPasswordSendOtpRequest): Response<GenericStatusResponse>

    @POST("patient/forgot/verify-otp/")
    suspend fun forgotPasswordVerifyOtp(@Body request: ForgotPasswordVerifyOtpRequest): Response<GenericStatusResponse>

    @POST("patient/forgot/reset-password/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<GenericStatusResponse>
    
    @POST("patient/change-password/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<GenericStatusResponse>

    @POST("patient/delete/")
    suspend fun deletePatient(@Body request: Map<String, String>): Response<GenericStatusResponse>

    @POST("patient/account-info/")
    suspend fun getPatientAccountInfo(@Body request: Map<String, String>): Response<AccountInfoResponse>

    // --- Patient Health ---
    @POST("patient/health/create/")
    suspend fun createHealthProfile(@Body request: PatientHealthProfileRequest): Response<GenericStatusResponse>

    @POST("patient/health/get/")
    suspend fun getHealthProfile(@Body request: Map<String, String>): Response<PatientHealthProfileResponse> // {"patient_id": "..."}

    @retrofit2.http.Multipart
    @PUT("patient/health/update/")
    suspend fun updateHealthProfile(
        @retrofit2.http.PartMap partMap: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @retrofit2.http.Part profilePicture: okhttp3.MultipartBody.Part?
    ): Response<GenericStatusResponse>

    // --- Reports ---
    @GET("patient/reports/")
    suspend fun listReports(@retrofit2.http.Query("patient_id") patientId: String): Response<List<MedicalReportResponse>>
    
    // --- Doctor Auth ---
    @POST("doctor/register/")
    suspend fun registerDoctor(@Body request: DoctorRegisterRequest): Response<AuthResponse>

    @POST("doctor/verify-otp/")
    suspend fun verifyDoctorOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("doctor/login/")
    suspend fun loginDoctor(@Body request: DoctorLoginRequest): Response<AuthResponse>
    
    @POST("doctor/forgot/send-otp/")
    suspend fun doctorForgotPasswordSendOtp(@Body request: ForgotPasswordSendOtpRequest): Response<GenericStatusResponse>

    @POST("doctor/forgot/verify-otp/")
    suspend fun doctorForgotPasswordVerifyOtp(@Body request: ForgotPasswordVerifyOtpRequest): Response<GenericStatusResponse>
    
    @POST("doctor/forgot/reset-password/")
    suspend fun doctorResetPassword(@Body request: ResetPasswordRequest): Response<GenericStatusResponse>

    @POST("doctor/change-password/")
    suspend fun doctorChangePassword(@Body request: ChangePasswordRequest): Response<GenericStatusResponse>

    @POST("doctor/delete/")
    suspend fun deleteDoctor(@Body request: Map<String, String>): Response<GenericStatusResponse>

    @POST("doctor/account-info/")
    suspend fun getDoctorAccountInfo(@Body request: Map<String, String>): Response<AccountInfoResponse>

    // --- Doctor Features ---
    @POST("doctor/profile/create/")
    suspend fun createDoctorProfile(@Body request: DoctorProfileRequest): Response<GenericStatusResponse>

    @POST("doctor/profile/get/")
    suspend fun getDoctorProfile(@Body request: Map<String, String>): Response<DoctorProfileResponse>

    @POST("doctor/patient-overview/")
    suspend fun getPatientOverview(@Body request: Map<String, String>): Response<PatientOverviewResponse> // {"patient_id": "..."}

    @POST("doctor/session-detail/")
    suspend fun getSessionDetail(@Body request: Map<String, Int>): Response<SessionDetailResponse> // {"session_id": 123}

    @retrofit2.http.Multipart
    @PUT("doctor/profile/update/")
    suspend fun updateDoctorProfile(
        @retrofit2.http.PartMap partMap: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>,
        @retrofit2.http.Part profilePicture: okhttp3.MultipartBody.Part?
    ): Response<GenericStatusResponse>

    @POST("doctor/save-viewed-patient/")
    suspend fun saveViewedPatient(@Body request: Map<String, String>): Response<GenericStatusResponse> // {"doc_id": "...", "patient_id": "...", "patient_name": "..."}

    @POST("doctor/list-viewed-patients/")
    suspend fun listViewedPatients(@Body request: Map<String, String>): Response<List<ViewedPatientResponse>> // {"doc_id": "..."}

    // --- AI / Symptom Selection ---
    @GET("ai/symptoms/")
    suspend fun getSymptoms(): Response<SymptomsResponse>

    @POST("ai/save-symptoms/")
    suspend fun saveSymptoms(@Body request: Map<String, Any>): Response<GenericStatusResponse> // {"symptoms": ["..."], "patient_id": "..."}

    @POST("ai/questions/")
    suspend fun getQuestions(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<QuestionsResponse> // {"patient_id": "...", "symptoms": [...]}

    @POST("ai/analyze/")
    suspend fun analyzeAI(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<AIAnalysisResponse> // {"patient_id": "...", "answers": {...}, "symptoms": [...]}

    @retrofit2.http.Multipart
    @POST("ai/analyze-file/")
    suspend fun analyzeFile(
        @retrofit2.http.Part("patient_id") patientId: okhttp3.RequestBody,
        @retrofit2.http.Part("description") description: okhttp3.RequestBody,
        @retrofit2.http.Part("category") category: okhttp3.RequestBody,
        @retrofit2.http.Part("symptoms") symptoms: okhttp3.RequestBody,
        @retrofit2.http.Part("answers") answers: okhttp3.RequestBody,
        @retrofit2.http.Part file: List<okhttp3.MultipartBody.Part>
    ): Response<FileAnalysisResponse>

    @POST("chat/start-session/")
    suspend fun startSession(@Body request: Map<String, String>): Response<SessionResponse>

    @retrofit2.http.Multipart
    @POST("patient/upload-report/")
    suspend fun uploadReport(
        @retrofit2.http.Part("patient_id") patientId: okhttp3.RequestBody,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part
    ): Response<FileUploadResponse>

    @POST("patient/reports/{report_id}/analyze/")
    suspend fun runAnalysis(@retrofit2.http.Path("report_id") reportId: Int): Response<FileAnalysisResponse>

    @GET("patient/reports/{report_id}/")
    suspend fun getReportAnalysis(@retrofit2.http.Path("report_id") reportId: Int): Response<FileAnalysisResponse>
    
    @GET("chat/history/")
    suspend fun getChatHistory(@retrofit2.http.Query("patient_id") patientId: String): Response<ChatHistoryListResponse>
}
