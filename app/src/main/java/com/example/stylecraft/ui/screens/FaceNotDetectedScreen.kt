package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceNotDetectedScreen(navController: NavController) {
    ErrorTemplateScreen(
        navController = navController,
        title = "Face Not Detected",
        message = "We couldn't detect a face in the image.\nPlease try again, making sure your face is well centered in the frame.",
        illustrationRes = R.drawable.ic_launcher_foreground, // TODO: add from Figma
        primaryText = "Try Again",
        onPrimaryClick = { navController.popBackStack(Routes.SCAN_FACE, inclusive = false) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoorLightingScreen(navController: NavController) {
    ErrorTemplateScreen(
        navController = navController,
        title = "Poor Lighting Detected",
        message = "The lighting is too dim. For best results, make sure your face is evenly lit and free of strong shadows.",
        illustrationRes = R.drawable.ic_launcher_foreground, // TODO
        primaryText = "Improve Light",
        secondaryText = "Continue anyway",
        onPrimaryClick = { navController.popBackStack(Routes.SCAN_FACE, inclusive = false) },
        onSecondaryClick = { navController.navigate(Routes.SCAN_PROGRESS) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleFacesScreen(navController: NavController) {
    ErrorTemplateScreen(
        navController = navController,
        title = "Multiple Faces Detected",
        message = "We detected more than one face in the image.\nPlease try again with only one person in the frame.",
        illustrationRes = R.drawable.ic_launcher_foreground, // TODO
        primaryText = "Try Again",
        onPrimaryClick = { navController.popBackStack(Routes.SCAN_FACE, inclusive = false) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorTemplateScreen(
    navController: NavController,
    title: String,
    message: String,
    illustrationRes: Int,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color(0xFF1C1B1F)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Image(
                painter = painterResource(id = illustrationRes),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onPrimaryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(primaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            if (secondaryText != null && onSecondaryClick != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onSecondaryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6200EE))
                ) {
                    Text(secondaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
