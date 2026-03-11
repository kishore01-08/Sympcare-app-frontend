package com.simats.sympcareai.data.response

import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("session_id") val sessionId: Int
)
