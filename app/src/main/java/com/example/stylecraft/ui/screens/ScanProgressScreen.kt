package com.example.stylecraft.ui.screens

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stylecraft.core.FaceResultPrefs
import com.example.stylecraft.core.copyUriToTempFile
import com.example.stylecraft.data.api.FaceShapeResponse
import com.example.stylecraft.data.api.RetrofitClient
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException

@Composable
fun ScanProgressScreen(
    navController: NavController,
    imageUriString: String?
) {
    val context = LocalContext.current
    val imageUri = imageUriString?.let { Uri.parse(it) }

    var faceShape by remember { mutableStateOf("OVAL") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Loader animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Fake progress while loading
    LaunchedEffect(isLoading) {
        if (isLoading) {
            for (i in 0..100) {
                progress = i / 100f
                delay(30)
            }
        }
    }

    // API call
    LaunchedEffect(imageUriString) {
        if (imageUri != null) {
            try {
                val file = copyUriToTempFile(context, imageUri) ?: run {
                    errorMessage = "Could not read image."
                    isLoading = false
                    return@LaunchedEffect
                }

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = file.name,
                    body = requestFile
                )

                val response = RetrofitClient.apiService.analyzeFaceShape(imagePart)
                if (response.isSuccessful) {
                    val result = response.body() ?: FaceShapeResponse("OVAL", 0.9f)
                    faceShape = result.faceShape
                    FaceResultPrefs.saveFaceShape(context, faceShape, result.confidence)

                    // Navigate to result screen; that screen must have its own Scaffold+background
                    navController.navigate("${Routes.FACE_RESULT}/$faceShape") {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                } else {
                    errorMessage = "Server error ${response.code()}. Please try again."
                }
            } catch (e: IOException) {
                errorMessage = "Network error. Please check your connection and try again."
            } catch (e: Exception) {
                errorMessage = "Something went wrong while analyzing the photo."
            } finally {
                isLoading = false
            }
        } else {
            errorMessage = "No image selected."
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                // ONLY this screen’s background; next screen uses its own Scaffold containerColor
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF5F0FF), // light lavender
                                    Color(0xFFE0D4FF)  // soft pastel purple
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        // Animated scanning icon
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(pulseScale)
                                .rotate(rotation)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.4f),
                                    radius = size.minDimension / 2
                                )
                                drawArc(
                                    color = Color(0xFF5A2DFF),
                                    startAngle = -90f,
                                    sweepAngle = progress * 360f,
                                    useCenter = false,
                                    style = Stroke(
                                        width = 8.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Analyzing your face...",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF1C1B1F),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedLoadingDots()

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF5A2DFF)
                        )
                    }
                }
            }

            errorMessage != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("Retake / Choose Another Photo")
                    }
                }
            }

            else -> {
                // Fallback: if loading finished but navigation not triggered (rare)
                LaunchedEffect(faceShape) {
                    navController.navigate("${Routes.FACE_RESULT}/$faceShape") {
                        popUpTo(Routes.SCAN_PROGRESS) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedLoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot $index"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = Color(0xFF5A2DFF).copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}
