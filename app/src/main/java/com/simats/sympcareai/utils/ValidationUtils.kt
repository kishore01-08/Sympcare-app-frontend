package com.simats.sympcareai.utils

object ValidationUtils {
    /**
     * Validates that a password:
     * - Is at least 8 characters long
     * - Contains at least one uppercase letter
     * - Contains at least one number
     * - Contains at least one special character
     */
    fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}\$".toRegex()
        return passwordRegex.matches(password)
    }

    fun hasMinLength(password: String) = password.length >= 8
    fun hasUpperCase(password: String) = password.any { it.isUpperCase() }
    fun hasDigit(password: String) = password.any { it.isDigit() }
    fun hasSpecialChar(password: String) = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }

    /**
     * Validates that an email is a valid Gmail address.
     */
    fun isValidGmail(email: String): Boolean {
        val gmailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$".toRegex()
        return gmailRegex.matches(email)
    }

    fun getPasswordErrorMessage(): String {
        return "Password must be at least 8 characters long, contain one uppercase letter, one number, and one special character."
    }

    fun getEmailErrorMessage(): String {
        return "Please enter a valid Gmail address (e.g., user@gmail.com)."
    }
}
