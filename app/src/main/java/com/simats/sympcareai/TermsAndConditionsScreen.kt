package com.simats.sympcareai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Terms of Service", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Last Updated: February 2026",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TermsSection(
                        title = "1. Introduction",
                        content = "Welcome to Sympcare AI. By accessing or using our mobile application, you agree to be bound by these Terms of Service. If you do not agree to all of these terms, do not use this application."
                    )

                    TermsSection(
                        title = "2. Medical Disclaimer (Critical)",
                        content = "Sympcare AI provides preliminary health assessments and symptom tracking based on artificial intelligence. \n\n" +
                                "THE APP DOES NOT PROVIDE MEDICAL ADVICE, DIAGNOSIS, OR TREATMENT.\n\n" +
                                "The information provided by this app is for informational purposes only and should not be used as a substitute for professional medical advice. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition. Never disregard professional medical advice or delay in seeking it because of something you have read on this app."
                    )
                    
                    TermsSection(
                        title = "3. User Accounts",
                        content = "You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account. You agree to provide accurate and complete information when creating an account."
                    )

                    TermsSection(
                        title = "4. Data Privacy",
                        content = "Your privacy is important to us. We collect and process your health data in accordance with our Privacy Policy. All personal health information is encrypted and stored securely. We do not sell your personal data to third parties."
                    )
                    
                    TermsSection(
                        title = "5. Limitation of Liability",
                        content = "In no event shall Sympcare AI or its developers be liable for any indirect, incidental, special, consequential, or punitive damages, including without limitation, loss of profits, data, use, goodwill, or other intangible losses, resulting from your access to or use of or inability to access or use the application."
                    )
                    
                     TermsSection(
                        title = "6. Changes to Terms",
                        content = "We reserve the right to modify these terms at any time. We will notify you of any changes by posting the new Terms on this page. You are advised to review this page periodically for any changes."
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}
