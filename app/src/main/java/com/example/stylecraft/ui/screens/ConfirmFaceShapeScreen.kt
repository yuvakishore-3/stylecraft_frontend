package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.stylecraft.core.FaceShapeUtils
import com.example.stylecraft.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmFaceShapeScreen(
    navController: NavController,
    faceShape: String?,
    imageUri: String?
) {
    val safeFaceShape = faceShape ?: "OVAL"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Your face Shape", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1C1B1F),
                    navigationIconContentColor = Color(0xFF1C1B1F)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Face Image with Overlay
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFF6200EE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Captured face",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = FaceShapeUtils.getFaceShapeEmoji(safeFaceShape),
                        fontSize = 120.sp
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Face Shape Display
            Text(
                text = safeFaceShape,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Does this look correct?",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            // Confirm Button
            Button(
                onClick = {
                    navController.navigate("${Routes.RECOMMENDATIONS}/$safeFaceShape")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Text(
                    text = "Yes, Confirm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            // Pick Manually Button
            OutlinedButton(
                onClick = {
                    // Show face shape picker dialog
                    navController.navigate(Routes.FACE_RESULT + "/$safeFaceShape")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6200EE)
                )
            ) {
                Text(
                    text = "No, Pick Manually",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
