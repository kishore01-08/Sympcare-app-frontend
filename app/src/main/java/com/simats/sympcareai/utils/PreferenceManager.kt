package com.simats.sympcareai.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SympCarePrefs", Context.MODE_PRIVATE)

    fun savePatientCredentials(patientId: String, password: String, rememberMe: Boolean) {
        sharedPreferences.edit().apply {
            putString("patient_id", if (rememberMe) patientId else "")
            putString("patient_password", if (rememberMe) password else "")
            putBoolean("remember_patient", rememberMe)
            apply()
        }
    }

    fun getPatientCredentials(): Triple<String, String, Boolean> {
        val id = sharedPreferences.getString("patient_id", "") ?: ""
        val pass = sharedPreferences.getString("patient_password", "") ?: ""
        val remember = sharedPreferences.getBoolean("remember_patient", false)
        return Triple(id, pass, remember)
    }

    fun saveDoctorCredentials(docId: String, password: String, rememberMe: Boolean) {
        sharedPreferences.edit().apply {
            putString("doc_id", if (rememberMe) docId else "")
            putString("doc_password", if (rememberMe) password else "")
            putBoolean("remember_doctor", rememberMe)
            apply()
        }
    }

    fun getDoctorCredentials(): Triple<String, String, Boolean> {
        val id = sharedPreferences.getString("doc_id", "") ?: ""
        val pass = sharedPreferences.getString("doc_password", "") ?: ""
        val remember = sharedPreferences.getBoolean("remember_doctor", false)
        return Triple(id, pass, remember)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
