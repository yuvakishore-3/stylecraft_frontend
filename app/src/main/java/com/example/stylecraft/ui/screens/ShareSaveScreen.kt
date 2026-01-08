package com.example.stylecraft.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.stylecraft.core.FaceResultPrefs
import com.example.stylecraft.ui.theme.FigmaPurple
import com.example.stylecraft.R
import java.io.File

data class ShareOption(
    val name: String,
    val iconRes: Int,
    val packageName: String? = null // null means generic share
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSaveScreen(navController: NavController) {
    val context = LocalContext.current
    val lastImagePath by FaceResultPrefs.lastImagePathFlow(context).collectAsState(null)

    val shareOptions = listOf(
        ShareOption("TikTok", R.drawable.ic_tiktok, "com.zhiliaoapp.musically"),
        ShareOption("Facebook", R.drawable.ic_facebook, "com.facebook.katana"),
        ShareOption("WhatsApp", R.drawable.ic_whatsapp, "com.whatsapp"),
        ShareOption("Instagram", R.drawable.ic_instagram, "com.instagram.android"),
        ShareOption("Pinterest", R.drawable.ic_pinterest, "com.pinterest"),
        ShareOption("Snapchat", R.drawable.ic_snapchat, "com.snapchat.android"),
        ShareOption("Drive", R.drawable.ic_drive, "com.google.android.apps.docs"),
        ShareOption("Twitter", R.drawable.ic_twitter, "com.twitter.android"),
        ShareOption("Photos", R.drawable.ic_photos, null), // Save to gallery
        ShareOption("Other", R.drawable.ic_more, null) // Generic share
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Transformations", 
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
        ) {
            // Main Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF8FAFF))
            ) {
                if (!lastImagePath.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(Uri.parse(lastImagePath!!)),
                        contentDescription = "Final look",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FigmaPurple)
                    }
                }
            }

            // Share Sheet
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF16161E),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Share With",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(24.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(shareOptions) { option ->
                            ShareOptionItem(
                                option = option,
                                onClick = {
                                    shareImage(context, lastImagePath, option)
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FigmaPurple)
                    ) {
                        Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareOptionItem(option: ShareOption, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = option.iconRes),
                    contentDescription = option.name,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Text(
            option.name, 
            color = Color.Gray, 
            fontSize = 10.sp, 
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun shareImage(context: Context, imagePath: String?, option: ShareOption) {
    if (imagePath.isNullOrEmpty()) return
    
    try {
        val originalUri = Uri.parse(imagePath)
        val imageUri = if (originalUri.scheme == "file") {
            val file = File(originalUri.path!!)
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            originalUri
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        // If specific app package is specified, try to use it
        if (option.packageName != null) {
            shareIntent.setPackage(option.packageName)
            
            // Check if the app is installed
            val packageManager = context.packageManager
            val activities = packageManager.queryIntentActivities(shareIntent, 0)
            
            if (activities.isNotEmpty()) {
                context.startActivity(shareIntent)
            } else {
                // App not installed, show general share picker
                val chooser = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    },
                    "Share via"
                )
                context.startActivity(chooser)
            }
        } else {
            // Generic share
            val chooser = Intent.createChooser(shareIntent, "Share via")
            context.startActivity(chooser)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
