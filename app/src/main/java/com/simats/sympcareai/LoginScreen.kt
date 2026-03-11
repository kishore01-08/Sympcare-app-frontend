package com.simats.sympcareai

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    onDoctorSignUpClick: () -> Unit,
    onDoctorForgotPasswordClick: () -> Unit,
    onDoctorLoginSuccess: (String, String) -> Unit,
    onPatientSignUpClick: () -> Unit,
    onPatientForgotPasswordClick: () -> Unit,
    onPatientLoginSuccess: (String, String) -> Unit,
    onPatientProfileIncomplete: (String, String) -> Unit,
    initialPage: Int = 0 
) {
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val titles = listOf("Patient", "Doctor")

    // Dynamic background based on current page
    // Patient (0) -> Teal/Blue
    // Doctor (1) -> Red/Orange
    // We can animate this or just switch. For simplicity and performance, let's just switch or use the specific screen's background.
    // However, the designs usually have specific backgrounds. 
    // Since we are embedding the screens, let's let them handle their main background if possible, 
    // BUT the tabs need to be visible.
    // Actually, `PatientLoginScreen` and `DoctorLoginScreen` have `fillMaxSize` and their own backgrounds.
    // If we put them in a Pager, they will fill the pager content.
    // We need to overlay the Tabs on top of them, or put the tabs above.
    
    // Let's put Tabs at the top, floating or fixed.
    
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PatientLoginScreen(
                    onSignUpClick = onPatientSignUpClick,
                    onForgotPasswordClick = onPatientForgotPasswordClick,
                    onLoginSuccess = { id, name -> onPatientLoginSuccess(id, name) },
                    onProfileIncomplete = { id, name -> onPatientProfileIncomplete(id, name) }
                )
                1 -> DoctorLoginScreen(
                    onSignUpClick = onDoctorSignUpClick,
                    onForgotPasswordClick = onDoctorForgotPasswordClick,
                    onLoginSuccess = { id, name -> onDoctorLoginSuccess(id, name) }
                )
            }
        }

        // Custom Tab Indicator at the top
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp) // Below status bar
                .width(240.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.2f)), // Semi-transparent container
            verticalAlignment = Alignment.CenterVertically
        ) {
            titles.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
