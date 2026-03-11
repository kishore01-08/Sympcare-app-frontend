package com.simats.sympcareai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBottomNavigationBar(
    currentScreen: Screen,
    onNavigateTo: (Screen) -> Unit,
    isDoctor: Boolean = false
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF009688)
    ) {
        if (isDoctor) {
            // Doctor Navigation Items
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = currentScreen == Screen.DoctorHome,
                onClick = { onNavigateTo(Screen.DoctorHome) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF009688),
                    selectedTextColor = Color(0xFF009688),
                    indicatorColor = Color(0xFFE0F2F1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Groups, contentDescription = "Patients") },
                label = { Text("Patients") },
                // Placeholder screen for now, or maybe use existing active patients list equivalent
                selected = currentScreen == Screen.DoctorPatients, 
                onClick = { onNavigateTo(Screen.DoctorPatients) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF009688),
                    selectedTextColor = Color(0xFF009688),
                    indicatorColor = Color(0xFFE0F2F1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                selected = currentScreen == Screen.DoctorSettings,
                onClick = { onNavigateTo(Screen.DoctorSettings) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF009688),
                    selectedTextColor = Color(0xFF009688),
                    indicatorColor = Color(0xFFE0F2F1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        } else {
            // Patient Navigation Items
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = currentScreen == Screen.PatientHome,
                onClick = { onNavigateTo(Screen.PatientHome) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF009688),
                    selectedTextColor = Color(0xFF009688),
                    indicatorColor = Color(0xFFE0F2F1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Refresh, contentDescription = "History") },
                label = { Text("History") },
                selected = currentScreen == Screen.ChatHistory,
                onClick = { onNavigateTo(Screen.ChatHistory) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF009688),
                    selectedTextColor = Color(0xFF009688),
                    indicatorColor = Color(0xFFE0F2F1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                selected = currentScreen == Screen.PatientSettings,
                onClick = { onNavigateTo(Screen.PatientSettings) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF009688),
                    selectedTextColor = Color(0xFF009688),
                    indicatorColor = Color(0xFFE0F2F1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
