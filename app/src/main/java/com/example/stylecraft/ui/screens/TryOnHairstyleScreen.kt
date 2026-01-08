package com.example.stylecraft.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.stylecraft.core.FaceResultPrefs
import com.example.stylecraft.core.FilterPrefs
import com.example.stylecraft.core.FilterSettings
import com.example.stylecraft.data.api.RetrofitClient
import com.example.stylecraft.navigation.Routes
import com.example.stylecraft.ui.theme.FigmaPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Color.Companion.Pink: Color get() = Color(0xFFFF4081)

enum class AdjustmentType {
    BRIGHTNESS, CONTRAST, SATURATION, HIGHLIGHT, SHADOW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TryOnHairstyleScreen(
    navController: NavController,
    styleId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lastImagePath by FaceResultPrefs.lastImagePathFlow(context).collectAsState(null)
    
    // Filter state values
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(0f) }
    var highlight by remember { mutableFloatStateOf(0f) }
    var shadow by remember { mutableFloatStateOf(0f) }
    
    var activeAdjustment by remember { mutableStateOf(AdjustmentType.BRIGHTNESS) }
    var activeTab by remember { mutableStateOf("Adjust") }
    var selectedColorTint by remember { mutableStateOf<Color?>(null) }
    
    // Hair processing state
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingError by remember { mutableStateOf<String?>(null) }
    var processingJob by remember { mutableStateOf<Job?>(null) }

    // Get current slider value based on active adjustment
    val currentSliderValue = when (activeAdjustment) {
        AdjustmentType.BRIGHTNESS -> brightness
        AdjustmentType.CONTRAST -> contrast
        AdjustmentType.SATURATION -> saturation
        AdjustmentType.HIGHLIGHT -> highlight
        AdjustmentType.SHADOW -> shadow
    }
    
    // Function to apply hair adjustments via backend
    fun applyHairAdjustments() {
        val imagePath = lastImagePath ?: return
        
        // Cancel any previous job
        processingJob?.cancel()
        
        processingJob = scope.launch {
            // Debounce - wait a bit before processing
            delay(300)
            
            isProcessing = true
            processingError = null
            
            try {
                val result = withContext(Dispatchers.IO) {
                    // Get the file from the URI
                    val uri = Uri.parse(imagePath)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File.createTempFile("hair_adjust_", ".jpg", context.cacheDir)
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    // Convert Color to hex string if selected
                    val colorHex = selectedColorTint?.let { color ->
                        String.format(
                            "%02X%02X%02X",
                            (color.red * 255).toInt(),
                            (color.green * 255).toInt(),
                            (color.blue * 255).toInt()
                        )
                    }
                    
                    // Create multipart request
                    val imagePart = MultipartBody.Part.createFormData(
                        "image",
                        tempFile.name,
                        tempFile.asRequestBody("image/jpeg".toMediaType())
                    )
                    
                    val brightnessBody = brightness.toString().toRequestBody("text/plain".toMediaType())
                    val contrastBody = contrast.toString().toRequestBody("text/plain".toMediaType())
                    val saturationBody = saturation.toString().toRequestBody("text/plain".toMediaType())
                    val highlightBody = highlight.toString().toRequestBody("text/plain".toMediaType())
                    val shadowBody = shadow.toString().toRequestBody("text/plain".toMediaType())
                    val colorBody = colorHex?.toRequestBody("text/plain".toMediaType())
                    val colorIntensityBody = "0.6".toRequestBody("text/plain".toMediaType())
                    
                    // Call the API
                    val response = RetrofitClient.apiService.applyHairAdjustments(
                        imagePart,
                        brightnessBody,
                        contrastBody,
                        saturationBody,
                        highlightBody,
                        shadowBody,
                        colorBody,
                        if (colorHex != null) colorIntensityBody else null
                    )
                    
                    // Clean up temp file
                    tempFile.delete()
                    
                    if (response.isSuccessful && response.body() != null) {
                        // Decode base64 image
                        val base64Image = response.body()!!.image
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } else {
                        null
                    }
                }
                
                if (result != null) {
                    processedBitmap = result
                } else {
                    processingError = "Failed to process image"
                }
            } catch (e: Exception) {
                if (e.message?.contains("cancel", ignoreCase = true) != true) {
                    processingError = "Error: ${e.message}"
                }
            } finally {
                isProcessing = false
            }
        }
    }
    
    // Trigger processing when adjustments or color change
    LaunchedEffect(brightness, contrast, saturation, highlight, shadow, selectedColorTint) {
        // Only process if there's something to apply
        if (brightness != 0f || contrast != 0f || saturation != 0f || 
            highlight != 0f || shadow != 0f || selectedColorTint != null) {
            applyHairAdjustments()
        } else {
            // Reset to original
            processedBitmap = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Try On Hairstyle", 
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
                .background(Color.White)
        ) {
            // Main Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF8FAFF))
            ) {
                // Show processed image if available, otherwise show original
                when {
                    processedBitmap != null -> {
                        // Show the hair-adjusted image from backend
                        Image(
                            bitmap = processedBitmap!!.asImageBitmap(),
                            contentDescription = "Hair Adjusted Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    !lastImagePath.isNullOrEmpty() -> {
                        // Show original image
                        Image(
                            painter = rememberAsyncImagePainter(Uri.parse(lastImagePath!!)),
                            contentDescription = "User Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = FigmaPurple)
                        }
                    }
                }
                
                // Loading overlay
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Processing hair...",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Error message
                if (processingError != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(
                                Color.Red.copy(alpha = 0.9f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            processingError!!,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Bottom Control Panel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF16161E),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Toolbar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { 
                            // Reset all values
                            brightness = 0f
                            contrast = 0f
                            saturation = 0f
                            highlight = 0f
                            shadow = 0f
                            selectedColorTint = null
                            processedBitmap = null
                            processingError = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Reset", tint = Color.White)
                        }
                        IconButton(onClick = { 
                            // Undo last change - reset current adjustment
                            when (activeAdjustment) {
                                AdjustmentType.BRIGHTNESS -> brightness = 0f
                                AdjustmentType.CONTRAST -> contrast = 0f
                                AdjustmentType.SATURATION -> saturation = 0f
                                AdjustmentType.HIGHLIGHT -> highlight = 0f
                                AdjustmentType.SHADOW -> shadow = 0f
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Undo", tint = Color.White)
                        }
                        Text(
                            text = if (activeTab == "Adjust") {
                                when (activeAdjustment) {
                                    AdjustmentType.BRIGHTNESS -> "Brightness"
                                    AdjustmentType.CONTRAST -> "Contrast"
                                    AdjustmentType.SATURATION -> "Saturation"
                                    AdjustmentType.HIGHLIGHT -> "Highlight"
                                    AdjustmentType.SHADOW -> "Shadow"
                                }
                            } else "Hair Colour",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { 
                            // Save filter settings before navigating
                            scope.launch {
                                FilterPrefs.saveFilterSettings(
                                    context,
                                    FilterSettings(
                                        brightness = brightness,
                                        contrast = contrast,
                                        saturation = saturation,
                                        highlight = highlight,
                                        shadow = shadow,
                                        colorTint = selectedColorTint?.value?.toLong()
                                    )
                                )
                                
                                // Save processed bitmap if available
                                processedBitmap?.let { bitmap ->
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val processedFile = File(context.cacheDir, "processed_hair_${System.currentTimeMillis()}.jpg")
                                            processedFile.outputStream().use { out ->
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                            }
                                            // Update the saved image path to the processed image
                                            FaceResultPrefs.saveLastImagePath(context, Uri.fromFile(processedFile).toString())
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                                
                                // Navigate after saving
                                withContext(Dispatchers.Main) {
                                    navController.navigate(Routes.TRANSFORMATIONS)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Apply", tint = Color.White)
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Ruler & Slider
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        // Ruler ticks
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            (-30..30 step 5).forEach { 
                                val height = if (it % 10 == 0) 12.dp else 6.dp
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(Modifier.width(1.dp).height(height).background(Color.Gray.copy(alpha = 0.5f)))
                                    if (it % 10 == 0) {
                                        Text(it.toString(), color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }
                        }
                        Slider(
                            value = currentSliderValue,
                            onValueChange = { newValue ->
                                when (activeAdjustment) {
                                    AdjustmentType.BRIGHTNESS -> brightness = newValue
                                    AdjustmentType.CONTRAST -> contrast = newValue
                                    AdjustmentType.SATURATION -> saturation = newValue
                                    AdjustmentType.HIGHLIGHT -> highlight = newValue
                                    AdjustmentType.SHADOW -> shadow = newValue
                                }
                            },
                            valueRange = -30f..30f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.Transparent,
                                inactiveTrackColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    if (activeTab == "Adjust") {
                        // Adjustment Tools
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ToolIcon(
                                Icons.Default.LightMode, 
                                "Brightness", 
                                activeAdjustment == AdjustmentType.BRIGHTNESS,
                                onClick = { activeAdjustment = AdjustmentType.BRIGHTNESS }
                            )
                            ToolIcon(
                                Icons.Default.Contrast, 
                                "Contrast", 
                                activeAdjustment == AdjustmentType.CONTRAST,
                                onClick = { activeAdjustment = AdjustmentType.CONTRAST }
                            )
                            ToolIcon(
                                Icons.Default.InvertColors, 
                                "Saturation", 
                                activeAdjustment == AdjustmentType.SATURATION,
                                onClick = { activeAdjustment = AdjustmentType.SATURATION }
                            )
                            ToolIcon(
                                Icons.Default.WbSunny, 
                                "Highlight", 
                                activeAdjustment == AdjustmentType.HIGHLIGHT,
                                onClick = { activeAdjustment = AdjustmentType.HIGHLIGHT }
                            )
                            ToolIcon(
                                Icons.Default.Tonality, 
                                "Shadow", 
                                activeAdjustment == AdjustmentType.SHADOW,
                                onClick = { activeAdjustment = AdjustmentType.SHADOW }
                            )
                        }
                    } else {
                        // Hair Color Swatches
                        val hairColors = listOf(
                            Color(0xFF4A3423), // Dark brown
                            Color(0xFF8B4513), // Saddle brown
                            Color(0xFFD2691E), // Chocolate
                            Color(0xFFF4A460), // Sandy brown
                            Color(0xFFFFD700), // Gold
                            Color(0xFFC0C0C0), // Silver
                            Color(0xFFFF4081), // Pink
                            Color(0xFF9C27B0), // Purple
                            Color(0xFF2196F3), // Blue
                            Color(0xFF4CAF50)  // Green
                        )
                        
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(hairColors) { color ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color)
                                        .border(
                                            width = if (selectedColorTint == color) 3.dp else 0.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { 
                                            selectedColorTint = if (selectedColorTint == color) null else color
                                        }
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                         Column(
                             horizontalAlignment = Alignment.CenterHorizontally,
                             modifier = Modifier.clickable { activeTab = "Adjust" }
                         ) {
                             Box(
                                 modifier = Modifier.size(40.dp).clip(CircleShape).background(if (activeTab == "Adjust") FigmaPurple else Color.Transparent),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White)
                             }
                         }
                         Column(
                             horizontalAlignment = Alignment.CenterHorizontally,
                             modifier = Modifier.clickable { activeTab = "Color" }
                         ) {
                             Box(
                                 modifier = Modifier.size(40.dp).clip(CircleShape).background(if (activeTab == "Color") FigmaPurple else Color.Transparent),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White)
                             }
                         }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolIcon(
    icon: ImageVector, 
    label: String, 
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) FigmaPurple else Color(0xFF23232D)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
        Text(
            text = label, 
            color = if (isSelected) Color.White else Color.Gray, 
            fontSize = 11.sp, 
            modifier = Modifier.padding(top = 6.dp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
