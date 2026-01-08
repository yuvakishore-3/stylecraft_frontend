package com.example.stylecraft.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.stylecraft.R
import com.example.stylecraft.core.UserProfilePrefs
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.launch
import com.example.stylecraft.ui.theme.FigmaPurple
import com.example.stylecraft.ui.theme.FigmaBg
import com.example.stylecraft.ui.theme.FigmaFieldBg
import com.example.stylecraft.ui.theme.FigmaIconBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val fullName by UserProfilePrefs.fullNameFlow(context).collectAsState("John Doe")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "My Profile", 
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Profile Image Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(100.dp),
                    color = Color.LightGray
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_capture_photo), // placeholder
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(FigmaPurple)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(32.dp))

            // Menu Items
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    label = "Profile",
                    onClick = { navController.navigate(Routes.EDIT_PROFILE) }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.FavoriteBorder,
                    label = "Favorite",
                    onClick = { navController.navigate(Routes.FAVORITES) }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    label = "Payment Method",
                    onClick = { /* Navigate to Payment */ }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "Privacy Policy",
                    onClick = { navController.navigate(Routes.PRIVACY_POLICY) }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Settings,
                    label = "Settings",
                    onClick = { navController.navigate(Routes.SETTINGS) }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.HelpOutline,
                    label = "Help",
                    onClick = { navController.navigate(Routes.HELP_CENTER) }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.ExitToApp,
                    label = "Logout",
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(Modifier.weight(1f))
            
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FigmaPurple)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        if (showLogoutDialog) {
            LogoutDialog(
                onDismiss = { showLogoutDialog = false },
                onLogout = {
                    showLogoutDialog = false
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
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
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = Color.Black
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = FigmaPurple.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val storedName by UserProfilePrefs.fullNameFlow(context).collectAsState("John Doe")
    val storedPhone by UserProfilePrefs.phoneFlow(context).collectAsState("+123 567 89000")
    val storedEmail by UserProfilePrefs.emailFlow(context).collectAsState("johndoe@example.com")
    val storedDob by UserProfilePrefs.dobFlow(context).collectAsState("DD / MM / YYY")

    var fullName by remember(storedName) { mutableStateOf(storedName) }
    var phone by remember(storedPhone) { mutableStateOf(storedPhone) }
    var email by remember(storedEmail) { mutableStateOf(storedEmail) }
    var dob by remember(storedDob) { mutableStateOf(storedDob) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Profile", 
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
                actions = {
                    IconButton(onClick = { /* Settings action */ }) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = FigmaPurple)
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Profile Image
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(100.dp),
                    color = Color.LightGray
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_capture_photo),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(FigmaPurple)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                EditField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
                EditField(label = "Phone Number", value = phone, onValueChange = { phone = it })
                EditField(label = "Email", value = email, onValueChange = { email = it })
                EditField(label = "Date Of Birth", value = dob, onValueChange = { dob = it })
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        UserProfilePrefs.updateProfile(context, fullName, phone, email, dob)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FigmaPurple)
            ) {
                Text("Update Profile", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { if (!it.contains("\n")) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FigmaFieldBg,
                unfocusedContainerColor = FigmaFieldBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.DarkGray
            )
        )
    }
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onLogout: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = FigmaPurple
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "are you sure you want to log out?",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E7FF)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", color = FigmaPurple)
                    }
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FigmaPurple),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Yes, Logout", color = Color.White)
                    }
                }
            }
        }
    }
}
