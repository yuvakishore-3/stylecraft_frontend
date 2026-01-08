package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.stylecraft.R
import com.example.stylecraft.core.OtpManager
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SignupScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var startSignupFlow by remember { mutableStateOf(false) }

    val strongPasswordRegex =
        remember {
            Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=-]).{8,}$")
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Big logo on top‑left (different asset)
        Image(
            painter = painterResource(id = R.drawable.ic_stylecraft_logo1),
            contentDescription = "StyleCraft logo",
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)
                .padding(top = 8.dp, bottom = 24.dp)
        )

        // Title + helper text
        Text(
            text = "Create a new account",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        Text(
            text = "Please put your information below to create a new account",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = null
            },
            label = { Text("Name") },
            isError = nameError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        if (nameError != null) {
            Text(
                text = nameError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email") },
            isError = emailError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Password") },
            isError = passwordError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Create account button – purple
        Button(
            onClick = {
                var hasError = false

                if (name.isBlank()) {
                    nameError = "Name is required"
                    hasError = true
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Enter a valid email address"
                    hasError = true
                }
                if (!strongPasswordRegex.containsMatchIn(password)) {
                    passwordError = "Use 8+ chars with upper, lower, number and symbol"
                    hasError = true
                }

                if (hasError) return@Button

                isSubmitting = true
                startSignupFlow = true
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
            Text("Create account", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        // Bottom "Sign in"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.LOGIN) { inclusive = false }
                }
            }) {
                Text("Sign in")
            }
        }
    }

    val context = LocalContext.current

    if (startSignupFlow) {
        LaunchedEffect(Unit) {
            delay(500)
            val randomOtp = OtpManager.generateOtp()
            Toast.makeText(context, "Demo OTP: $randomOtp", Toast.LENGTH_LONG).show()
            isSubmitting = false
            startSignupFlow = false
            navController.navigate(Routes.OTP) {
                popUpTo(Routes.SIGNUP) { inclusive = true }
            }
        }
    }
}
