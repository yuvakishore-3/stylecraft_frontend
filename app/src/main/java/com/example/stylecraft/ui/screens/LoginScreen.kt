package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.stylecraft.R
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var startLoginFlow by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    val strongPasswordRegex =
        remember {
            Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=-]).{8,}$")
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_stylecraft_logo1),
            contentDescription = "StyleCraft logo",
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)                      // bigger size
                .padding(top = 8.dp, bottom = 24.dp) // space below logo
        )

        // Title
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        Text(
            text = "Please put your information below to sign in to your account",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
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

        // Password with trailing eye icon (static)
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
                .padding(bottom = 4.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle password visibility"
                )
            }
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

        // Remember me + Forgot password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it }
                )
                Text("Remember me", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = { navController.navigate(Routes.PASSWORD_RECOVERY) }) {
                Text("Forgot Password?")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Sign in button – purple, full width, rounded
        Button(
            onClick = {
                var hasError = false

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
                startLoginFlow = true
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
            Text("Sign in", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        // Bottom "Create Account"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = { navController.navigate(Routes.SIGNUP) }) {
                Text("Create Account")
            }
        }
    }

    if (startLoginFlow) {
        LaunchedEffect(Unit) {
            delay(500)
            isSubmitting = false
            startLoginFlow = false

            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }
}
