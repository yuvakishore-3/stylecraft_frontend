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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylecraft.ui.theme.FigmaPurple
import com.example.stylecraft.ui.theme.FigmaIconBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("FAQ") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("Popular Topic") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Help Center", 
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
        ) {
            Spacer(Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                placeholder = { Text("Search...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = FigmaPurple,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(Modifier.height(24.dp))

            // Custom Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                TabButton(
                    selected = selectedTab == "FAQ",
                    label = "FAQ",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = "FAQ" }
                )
                TabButton(
                    selected = selectedTab == "Contact Us",
                    label = "Contact Us",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = "Contact Us" }
                )
            }

            Spacer(Modifier.height(24.dp))

            if (selectedTab == "FAQ") {
                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChipItem(selectedChip == "Popular Topic", "Popular Topic") { selectedChip = it }
                    FilterChipItem(selectedChip == "General", "General") { selectedChip = it }
                    FilterChipItem(selectedChip == "Services", "Services") { selectedChip = it }
                }

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    FaqItem("Lorem ipsum dolor sit amet?")
                    FaqItem("Lorem ipsum dolor sit amet?")
                    FaqItem("Lorem ipsum dolor sit amet?")
                    FaqItem("Lorem ipsum dolor sit amet?")
                    FaqItem("Lorem ipsum dolor sit amet?")
                    FaqItem("Lorem ipsum dolor sit amet?")
                }
            } else {
                // Contact Us List
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    ContactItem(Icons.Default.SupportAgent, "Customer Service")
                    ContactItem(Icons.Default.Language, "Website")
                    ContactItem(Icons.Default.Chat, "Whatsapp")
                    ContactItem(Icons.Default.Facebook, "Facebook")
                    ContactItem(Icons.Default.CameraAlt, "Instagram") // Placeholder for Instagram
                }
            }
        }
    }
}

@Composable
fun TabButton(selected: Boolean, label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) FigmaPurple else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else FigmaPurple,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun FilterChipItem(selected: Boolean, label: String, onClick: (String) -> Unit) {
    Surface(
        onClick = { onClick(label) },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) FigmaPurple else Color(0xFFEEF2FF),
        modifier = Modifier.height(32.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = if (selected) Color.White else FigmaPurple,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FaqItem(question: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(question, color = Color.Black, fontSize = 14.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = FigmaPurple)
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FigmaIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = FigmaPurple, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.Black)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
        }
    }
}
