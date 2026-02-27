package com.example.stylecraft.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylecraft.core.AppTheme
import com.example.stylecraft.core.ThemePrefs
import com.example.stylecraft.navigation.Routes
import com.example.stylecraft.ui.theme.FigmaPurple
import com.example.stylecraft.ui.theme.FigmaIconBg
import com.example.stylecraft.ui.theme.FigmaBg
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentTheme by ThemePrefs.themeFlow(context).collectAsState(initial = AppTheme.SYSTEM)
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        color = FigmaPurple, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FigmaPurple
                        )
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            SettingsMenuItem(
                icon = Icons.Default.DarkMode,
                label = "Theme",
                subtitle = when (currentTheme) {
                    AppTheme.LIGHT -> "Light"
                    AppTheme.DARK -> "Dark"
                    AppTheme.SYSTEM -> "System Default"
                },
                onClick = { showThemeDialog = true }
            )
            SettingsMenuItem(
                icon = Icons.Default.Lightbulb,
                label = "Notification Setting",
                onClick = { navController.navigate(Routes.NOTIFICATION_SETTINGS) }
            )
            SettingsMenuItem(
                icon = Icons.Default.Lock,
                label = "Password Manager",
                onClick = { navController.navigate(Routes.CHANGE_PASSWORD) }
            )
            SettingsMenuItem(
                icon = Icons.Default.Person,
                label = "Delete Account",
                onClick = { /* Handle delete account */ }
            )
        }
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text("Select Theme", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    ThemeOption(
                        name = "Light",
                        isSelected = currentTheme == AppTheme.LIGHT,
                        onClick = {
                            scope.launch {
                                ThemePrefs.setTheme(context, AppTheme.LIGHT)
                            }
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        name = "Dark",
                        isSelected = currentTheme == AppTheme.DARK,
                        onClick = {
                            scope.launch {
                                ThemePrefs.setTheme(context, AppTheme.DARK)
                            }
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        name = "System Default",
                        isSelected = currentTheme == AppTheme.SYSTEM,
                        onClick = {
                            scope.launch {
                                ThemePrefs.setTheme(context, AppTheme.SYSTEM)
                            }
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel", color = FigmaPurple)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun ThemeOption(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = FigmaPurple
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(FigmaIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FigmaPurple,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = FigmaPurple,
            modifier = Modifier.size(20.dp)
        )
    }
}
