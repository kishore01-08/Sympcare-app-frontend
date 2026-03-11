package com.simats.sympcareai.utils

import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val KOLKATA_ZONE = ZoneId.of("Asia/Kolkata")
    private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

    /**
     * Converts a UTC ISO-8601 string to Asia/Kolkata time zone string.
     * Expects input like "2026-02-26T10:07:49Z" or "2026-02-26T10:07:49.000Z"
     */
    fun formatToKolkataTime(utcString: String?, pattern: String = "dd MMM yyyy, hh:mm a"): String {
        if (utcString.isNullOrEmpty()) return "Unknown"
        
        return try {
            val utcTime = ZonedDateTime.parse(utcString)
            val kolkataTime = utcTime.withZoneSameInstant(KOLKATA_ZONE)
            val formatter = if (pattern == "dd MMM yyyy, hh:mm a") DEFAULT_FORMATTER else DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
            kolkataTime.format(formatter)
        } catch (e: Exception) {
            // Fallback for simple date strings like "2026-02-26" if they aren't ISO-8601
            try {
                if (utcString.contains("-") && !utcString.contains("T")) {
                    utcString // return as is if it's already a simple date
                } else {
                    utcString
                }
            } catch (ex: Exception) {
                utcString
            }
        }
    }
}
