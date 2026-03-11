package com.simats.sympcareai

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun PermissionRequestLogic() {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val permissionsToRequest = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            val allGranted = perms.values.all { it }
            if (!allGranted) {
                // Check if we should show rationale or if it's potentially permanently denied
                // Compose doesn't strictly tell us "permanently denied" directly here easily without
                // checking shouldShowRequestPermissionRationale before request.
                // For simplicity in this flow: if denied, we assume we might need to explain.
                // true "persistence" check logic usually requires Activity ref. 
                // We'll show a dialog to go to settings if denied.
                showPermissionRationale = true
            }
        }
    )

    LaunchedEffect(Unit) {
        val notGranted = permissionsToRequest.any {
            ContextCompat.checkSelfPermission(context, it) != PermissionChecker.PERMISSION_GRANTED
        }
        if (notGranted) {
            launcher.launch(permissionsToRequest)
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permissions Required") },
            text = { Text("SympCareAI needs Camera and Microphone permissions to function correctly (Video Consulations, Voice Assistant). Please enable them in Settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
