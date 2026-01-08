package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import com.example.stylecraft.R
import com.example.stylecraft.core.FaceShapeUtils
import com.example.stylecraft.data.api.AppDatabase
import com.example.stylecraft.data.api.ScanHistoryRepository
import com.example.stylecraft.navigation.Routes
import com.example.stylecraft.core.UserProfilePrefs

data class HomeBottomItem(
    val route: String,
    val label: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val items = listOf(
        HomeBottomItem(Routes.HOME, "Home", R.drawable.ic_home_scan_face),
        HomeBottomItem(Routes.HISTORY, "History", R.drawable.ic_home_upload_face),
        HomeBottomItem(Routes.FAVORITES, "Favourite", R.drawable.ic_home_upload_face),
        HomeBottomItem(Routes.SUBSCRIPTION, "Pro", R.drawable.ic_home_pro)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StyleCraft") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.PROFILE) }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                val items = listOf(
                    Routes.HOME,
                    Routes.HISTORY,
                    Routes.FAVORITES,
                    Routes.SUBSCRIPTION
                )

                items.forEach { route ->
                    val selected = currentRoute?.startsWith(route) == true
                    val icon = when (route) {
                        Routes.HOME -> Icons.Filled.Home
                        Routes.HISTORY -> Icons.Filled.History
                        Routes.FAVORITES -> Icons.Filled.FavoriteBorder
                        Routes.SUBSCRIPTION -> Icons.Filled.Star
                        else -> Icons.Filled.Home
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(route) {
                                    popUpTo(Routes.HOME) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = route
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { padding ->
        HomeContent(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val repository = remember { ScanHistoryRepository(AppDatabase.getDatabase(context)) }
    val latestScan = repository.getLatestScan().collectAsState(initial = null)
    val scanCount = repository.getScanCount().collectAsState(initial = 0)

    val fullName by UserProfilePrefs.fullNameFlow(context).collectAsState("John Doe")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = "Hey, $fullName!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1B1F)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Discover your perfect hairstyle",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        latestScan.value?.let { scan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    navController.navigate("${Routes.FACE_RESULT}/${scan.faceShape}")
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FaceShapeUtils.getFaceShapeEmoji(scan.faceShape),
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 56.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Latest Scan",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = scan.faceShape,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5A2DFF)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${(scan.confidence * 100).toInt()}% confident • ${
                                FaceShapeUtils.formatTimestamp(scan.timestamp)
                            }",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        if (scanCount.value > 0) {
            TextButton(
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    navController.navigate(Routes.HISTORY)
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🕐",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "View all ${scanCount.value} scans",
                    color = Color(0xFF5A2DFF),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        HomeActionCard(
            title = "Scan the Face",
            description = "Use your camera to scan your face and try on hairstyles in real-time.",
            iconRes = R.drawable.ic_home_scan_face,
            isPrimary = true
        ) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            navController.navigate(Routes.CAMERA_ACCESS)
        }

        Spacer(Modifier.height(16.dp))

        HomeActionCard(
            title = "Upload Photo",
            description = "Upload a photo from your gallery to try different hairstyles.",
            iconRes = R.drawable.ic_home_upload_face,
            isPrimary = false
        ) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            navController.navigate(Routes.UPLOAD_PHOTO)
        }
    }
}

@Composable
fun HomeActionCard(
    title: String,
    description: String,
    iconRes: Int,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val background = if (isPrimary) Color(0xFF5A2DFF) else Color.White
    val titleColor = if (isPrimary) Color.White else Color(0xFF1C1B1F)
    val descColor = if (isPrimary) Color.White.copy(alpha = 0.9f) else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimary) 4.dp else 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 20.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = descColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
