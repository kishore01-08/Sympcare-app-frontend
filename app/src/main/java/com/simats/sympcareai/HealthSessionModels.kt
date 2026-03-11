package com.simats.sympcareai

import androidx.compose.ui.graphics.Color

data class HealthQuestion(
    val id: Int,
    val text: String,
    val options: List<HealthOption>
)

data class HealthOption(
    val emoji: String,
    val label: String,
    val score: Int
)

data class HealthSession(
    val id: Int,
    val title: String,
    val date: String,
    val duration: String,
    val status: String, // Improved, Neutral, Needs Attention
    val statusColor: Color,
    val statusTextColor: Color,
    val score: Int = 0,
    val type: HealthAssessmentType
)

enum class HealthAssessmentType {
    MORNING_WELLNESS,
    EVENING_HEALTH,
    WEEKLY_MENTAL,
    PHYSICAL_ACTIVITY,
    DIET_NUTRITION
}

object HealthDataStore {
    val defaultOptions = listOf(
        HealthOption("😴", "Poor", 1),
        HealthOption("😕", "Fair", 2),
        HealthOption("😊", "Good", 3),
        HealthOption("😄", "Very Good", 4),
        HealthOption("🌟", "Excellent", 5)
    )

    val fullQuestionList = listOf(
        HealthQuestion(1, "How would you rate your overall physical condition today?", defaultOptions),
        HealthQuestion(2, "How would you rate your energy level today?", defaultOptions),
        HealthQuestion(3, "How would you rate your body strength today?", defaultOptions),
        HealthQuestion(4, "How would you rate your level of body pain or discomfort today?", defaultOptions),
        HealthQuestion(5, "How would you rate your muscle and joint comfort today?", defaultOptions),
        HealthQuestion(6, "How would you rate your breathing comfort today?", defaultOptions),
        HealthQuestion(7, "How would you rate your heart comfort (no chest pain or palpitations)?", defaultOptions),
        HealthQuestion(8, "How would you rate your blood circulation (no swelling or numbness)?", defaultOptions),
        HealthQuestion(9, "How would you rate your digestive comfort today?", defaultOptions),
        HealthQuestion(10, "How would you rate your appetite today?", defaultOptions),
        HealthQuestion(11, "How would you rate your hydration level today?", defaultOptions),
        HealthQuestion(12, "How would you rate your sleep quality last night?", defaultOptions),
        HealthQuestion(13, "How would you rate your physical mobility today?", defaultOptions),
        HealthQuestion(14, "How would you rate your balance and stability while walking?", defaultOptions),
        HealthQuestion(15, "How would you rate your endurance during physical activities?", defaultOptions),
        HealthQuestion(16, "How would you rate your recovery from physical effort?", defaultOptions),
        HealthQuestion(17, "How would you rate your immunity and resistance to illness recently?", defaultOptions),
        HealthQuestion(18, "How would you rate your ability to perform daily tasks without fatigue?", defaultOptions)
    )

    val mockSessions = emptyList<HealthSession>() // No longer used in the new direct flow
}
