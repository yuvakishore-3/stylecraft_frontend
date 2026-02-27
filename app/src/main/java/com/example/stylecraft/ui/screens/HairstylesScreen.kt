package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.stylecraft.core.FaceResultPrefs
import com.example.stylecraft.navigation.Routes
import com.example.stylecraft.ui.theme.FigmaPurple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HairstylesScreen(navController: NavController, faceShape: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shape = faceShape.uppercase()
    val persistentGender by FaceResultPrefs.genderFlow(context).collectAsState(initial = "Female")
    
    // Reload when gender or persistent state changes
    val hairstyles = remember(shape, persistentGender) { 
        sampleHairstyles(shape, persistentGender ?: "Female") 
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Recommended Hairstyles", 
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
            // Gender Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GenderOption(
                    text = "Female",
                    isSelected = persistentGender == "Female",
                    modifier = Modifier.weight(1f),
                    onClick = { 
                        scope.launch { 
                            FaceResultPrefs.saveFaceShape(context, shape, 0.9f, "Female") 
                        } 
                    }
                )
                GenderOption(
                    text = "Male",
                    isSelected = persistentGender == "Male",
                    modifier = Modifier.weight(1f),
                    onClick = { 
                        scope.launch { 
                            FaceResultPrefs.saveFaceShape(context, shape, 0.9f, "Male") 
                        } 
                    }
                )
            }
        
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hairstyles) { style ->
                    HairstyleListItem(style = style) {
                        if (style.isPro) {
                            navController.navigate(Routes.SUBSCRIPTION)
                        } else {
                            navController.navigate("${Routes.HAIRSTYLE_DETAIL}/${style.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HairstyleListItem(style: HairstyleUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(90.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = if (style.imageUrl.isNotEmpty()) style.imageUrl else style.imageRes
                    ),
                    contentDescription = style.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                if (style.isPro) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp)
                            .width(24.dp),
                        color = Color(0xFF4530B3),
                        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("P", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text("R", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text("O", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = style.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = style.mood,
                    fontSize = 13.sp,
                    color = Color.Gray.copy(alpha = 0.8f),
                    lineHeight = 18.sp,
                    maxLines = 2
                )
                
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "4.9",
                        fontSize = 13.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Try Now",
                        fontSize = 13.sp,
                        color = Color(0xFF4530B3),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
