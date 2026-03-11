package com.simats.sympcareai.data.request

import com.google.gson.annotations.SerializedName

data class PatientRegisterRequest(
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val email: String,
    val password: String
)

data class DoctorRegisterRequest(
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val email: String,
    val password: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class PatientLoginRequest(
    @SerializedName("patient_id") val patientId: String,
    val password: String
)

data class DoctorLoginRequest(
    @SerializedName("doc_id") val docId: String,
    val password: String
)

data class ForgotPasswordSendOtpRequest(
    val email: String
)

data class ForgotPasswordVerifyOtpRequest(
    val email: String,
    val otp: String
)

data class ResetPasswordRequest(
    val email: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("confirm_password") val confirmPassword: String,
    val otp: String? = null // Added for Doctor's one-step reset
)

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("confirm_password") val confirmPassword: String,
    @SerializedName("patient_id") val patientId: String? = null,
    @SerializedName("doc_id") val docId: String? = null
)
