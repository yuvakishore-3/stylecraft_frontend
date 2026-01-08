package com.example.stylecraft.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.stylecraft.core.FaceResultPrefs
import com.example.stylecraft.core.FaceHairstyleMatch
import com.example.stylecraft.navigation.Routes
import com.example.stylecraft.ui.theme.FigmaPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HairstyleDetailScreen(
    navController: NavController,
    styleId: String
) {
    val context = LocalContext.current
    val lastImagePath by FaceResultPrefs.lastImagePathFlow(context).collectAsState(null)
    val detectedFaceShape by FaceResultPrefs.faceShapeFlow(context).collectAsState(null)
    
    val style = remember(styleId) {
        sampleHairstyles().firstOrNull { it.id == styleId } ?: sampleHairstyles().first()
    }
    
    // Calculate dynamic match percentage based on detected face shape
    val matchPercentage = remember(detectedFaceShape, style.name) {
        FaceHairstyleMatch.calculateMatch(detectedFaceShape, style.name)
    }
    
    val matchDescription = remember(detectedFaceShape, style.name, matchPercentage) {
        FaceHairstyleMatch.getMatchDescription(detectedFaceShape, style.name, matchPercentage)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Hair Style Details", 
                        color = Color.Black, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // Large Image - Using captured photo/uploaded photo if available
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF8FAFF))
            ) {
                val painter = if (!lastImagePath.isNullOrEmpty()) {
                    // lastImagePath is now always a URI string
                    rememberAsyncImagePainter(Uri.parse(lastImagePath!!))
                } else {
                    rememberAsyncImagePainter(style.imageRes)
                }

                Image(
                    painter = painter,
                    contentDescription = style.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = style.name,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.Black
            )

            Text(
                text = "Face shape Match",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Dynamic match percentage
            Text(
                text = "${matchPercentage}% Match",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = if (matchPercentage >= 80) FigmaPurple else if (matchPercentage >= 60) Color(0xFFFF9800) else Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            // Dynamic description based on match
            Text(
                text = matchDescription,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("${Routes.TRY_ON_HAIRSTYLE}/${style.id}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FigmaPurple)
            ) {
                Text("Try Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
