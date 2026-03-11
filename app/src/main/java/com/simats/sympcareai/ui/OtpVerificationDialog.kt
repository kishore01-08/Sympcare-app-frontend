package com.simats.sympcareai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationDialog(
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit,
    primaryColor: Color
) {
    var otpValue by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
            isTimerRunning = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                "Verification Code",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "We have sent the verification code to your email address",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // OTP Input Rows
                val otpLength = 6
                BasicTextField(
                    value = otpValue,
                    onValueChange = {
                        if (it.length <= otpLength && it.all { char -> char.isDigit() }) {
                            otpValue = it
                            isError = false
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(otpLength) { index ->
                                val char = if (index < otpValue.length) otpValue[index].toString() else ""
                                val isFocused = otpValue.length == index
                                
                                Box(
                                    modifier = Modifier
                                        .size(40.dp) // Reduced size to fit 6 digits
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isError) Color.Red else if (isFocused) primaryColor else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        style = TextStyle(
                                            fontSize = 18.sp, // Reduced font size
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Invalid OTP. Please try again.", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTimerRunning) {
                        Text(
                            text = "Resend code in 00:${timerSeconds.toString().padStart(2, '0')}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    } else {
                        TextButton(onClick = { 
                            timerSeconds = 30
                            isTimerRunning = true
                            // TODO: Trigger resend logic
                        }) {
                            Text("Resend Code", color = primaryColor)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (otpValue.length == 6) {
                        onVerify(otpValue)
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
