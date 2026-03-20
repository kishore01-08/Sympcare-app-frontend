package com.simats.sympcareai.data.response

import com.google.gson.annotations.SerializedName

data class ChatServerResponse(
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("question")
    val question: String? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("answers")
    val answers: Map<String, String>? = null
)
