package com.example.stylecraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.stylecraft.core.AppTheme
import com.example.stylecraft.core.ThemePrefs
import com.example.stylecraft.navigation.AppNavHost
import com.example.stylecraft.ui.theme.StyleCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreference by ThemePrefs.themeFlow(this).collectAsState(initial = AppTheme.SYSTEM)
            
            val isDarkTheme = when (themePreference) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }
            
            StyleCraftTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {_ ->
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
