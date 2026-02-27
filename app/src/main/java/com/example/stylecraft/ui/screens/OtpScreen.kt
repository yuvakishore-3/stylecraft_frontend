package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.example.stylecraft.R
import com.example.stylecraft.core.OtpManager
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(navController: NavController) {
    val context = LocalContext.current
    var otp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // ... (logo and text remain same)
        Image(
            painter = painterResource(id = R.drawable.ic_stylecraft_logo1),
            contentDescription = "StyleCraft logo",
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)
                .padding(top = 8.dp, bottom = 24.dp)
        )

        Text(
            text = "OTP Authentication",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        Text(
            text = "Please enter the OTP sent to your email/phone",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = otp,
            onValueChange = {
                otp = it.filter { ch -> ch.isDigit() }.take(6)
                otpError = null
            },
            label = { Text("OTP") },
            isError = otpError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )

        if (otpError != null) {
            Text(
                text = otpError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Didn't receive the code? ",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Resend",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A2DFF)
                ),
                modifier = Modifier.clickable {
                    val currentOtp = OtpManager.getOtp()
                    Toast.makeText(context, "Resent Demo OTP: $currentOtp", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (!OtpManager.verifyOtp(otp)) {
                    val currentOtp = OtpManager.getOtp()
                    otpError = "Invalid OTP. Use $currentOtp"
                    return@Button
                }
                isSubmitting = true
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5A2DFF)
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Continue", color = Color.White)
        }
    }

    if (isSubmitting) {
// ...
        LaunchedEffect(Unit) {
            delay(500)
            isSubmitting = false
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.OTP) { inclusive = true }
            }
        }
    }
}
