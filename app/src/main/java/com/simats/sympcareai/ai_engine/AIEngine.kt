package com.simats.sympcareai.ai_engine

import androidx.compose.ui.graphics.Color

/**
 * Helper class for processing AI Analysis data on the frontend.
 * Mirrors the logic split found in the backend ai_engine folder.
 */
object AIEngine {

    /**
     * Maps a triage level (1, 2, 3) to a display color.
     */
    fun getTriageColor(level: Int): Color {
        return when (level) {
            1 -> Color(0xFFE53935) // High - Red
            2 -> Color(0xFFFFB300) // Medium - Orange
            3 -> Color(0xFF43A047) // Low - Green
            else -> Color.Gray
        }
    }

    /**
     * Maps a triage level to a descriptive text.
     */
    fun getTriageDescription(level: Int): String {
        return when (level) {
            1 -> "Urgent Attention Needed"
            2 -> "Follow-up Recommended"
            3 -> "Routine Monitoring"
            else -> "Not Determined"
        }
    }

    /**
     * Formats a probability value (0.0 - 1.0) into a percentage string.
     */
    fun formatProbability(prob: Float?): String {
        if (prob == null) return "N/A"
        return "${(prob * 100).toInt()}%"
    }

    /**
     * Prepares symptom data for display or logging.
     */
    fun getFormattedSymptomList(symptoms: List<String>): String {
        if (symptoms.isEmpty()) return "No symptoms selected"
        return symptoms.joinToString(", ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
    }
}
