package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylecraft.R
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(2000) // 2 seconds
        navController.navigate(Routes.ONBOARDING) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_stylecraft_logo),
            contentDescription = "StyleCraft logo",
            modifier = Modifier.size(220.dp)
        )

        // Bottom anchored text "Powered BY SIMATS"
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered BY",
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "SIMATS",
                style = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF5A2DFF), // Primary brand purple
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}
