package com.example.stylecraft.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylecraft.ui.theme.FigmaPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var generalNotify by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }
    var soundCall by remember { mutableStateOf(true) }
    var vibrate by remember { mutableStateOf(false) }
    var specialOffers by remember { mutableStateOf(false) }
    var payments by remember { mutableStateOf(true) }
    var promoDiscount by remember { mutableStateOf(false) }
    var cashback by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Notification Setting", 
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

            NotificationSwitchItem("General Notification", generalNotify) { generalNotify = it }
            NotificationSwitchItem("Sound", sound) { sound = it }
            NotificationSwitchItem("Sound Call", soundCall) { soundCall = it }
            NotificationSwitchItem("Vibrate", vibrate) { vibrate = it }
            NotificationSwitchItem("Special Offers", specialOffers) { specialOffers = it }
            NotificationSwitchItem("Payments", payments) { payments = it }
            NotificationSwitchItem("Promo And Discount", promoDiscount) { promoDiscount = it }
            NotificationSwitchItem("Cashback", cashback) { cashback = it }
        }
    }
}

@Composable
fun NotificationSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = FigmaPurple,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFDBEAFE),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
