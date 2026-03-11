package com.simats.sympcareai.data.request

import com.google.gson.annotations.SerializedName

data class PatientHealthProfileRequest(
    @SerializedName("patient_id") val patientId: String,
    val age: Int,
    val gender: String,
    val height: Float,
    val weight: Float,
    @SerializedName("blood_group") val bloodGroup: String,
    @SerializedName("existing_conditions") val existingConditions: String = ""
)

data class DoctorProfileRequest(
    @SerializedName("doc_id") val docId: String,
    @SerializedName("full_name") val fullName: String,
    val age: Int,
    val gender: String,
    val specialization: String,
    @SerializedName("profile_picture") val profilePicture: String? = null
)
