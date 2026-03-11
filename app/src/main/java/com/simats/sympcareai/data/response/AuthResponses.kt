package com.simats.sympcareai.data.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val status: String? = null,
    val error: String? = null,
    val doctor: String? = null,
    @SerializedName("patient_id") val patientId: String? = null,
    @SerializedName("full_name") val fullName: String? = null
)

data class GenericStatusResponse(
    val status: String? = null,
    val error: String? = null
)

data class AccountInfoResponse(
    @SerializedName("full_name") val fullName: String?,
    val email: String?,
    @SerializedName("patient_id") val patientId: String? = null,
    @SerializedName("doc_id") val docId: String? = null
)
