package com.example.healthtracker.ui.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.healthtracker.R
import com.example.healthtracker.viewmodel.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit = {},
    isDarkMode: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current

    val userEmail by loginViewModel.userEmail.collectAsState()
    val userPhotoUrl by loginViewModel.userPhotoUrl.collectAsState()
    val isGoogleLogin by loginViewModel.isGoogleLogin.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE8F5E8),
                            Color(0xFFF1F8E9)
                        )
                    )
                )
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {

                // PROFILE SECTION
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(Alignment.Top)
                        .padding(top = 40.dp)
                ) {
                    // PROFILE CARD
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = MaterialTheme.shapes.large,
                                clip = true
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp)
                        ) {
                            // ẢNH ĐẠI DIỆN
                            when {
                                isGoogleLogin && !userPhotoUrl.isNullOrEmpty() -> {
                                    AsyncImage(
                                        model = userPhotoUrl,
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(
                                                BorderStroke(3.dp, Color(0xFF4CAF50)),
                                                CircleShape
                                            )
                                    )
                                }
                                isGoogleLogin && userPhotoUrl.isNullOrEmpty() -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.default_avatar),
                                        contentDescription = "Default Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(
                                                BorderStroke(3.dp, Color(0xFF4CAF50)),
                                                CircleShape
                                            )
                                    )
                                }
                                else -> {
                                    val firstLetter = userEmail.firstOrNull()?.uppercase() ?: "U"
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF4CAF50),
                                                        Color(0xFF2E7D32)
                                                    )
                                                )
                                            )
                                            .border(
                                                BorderStroke(3.dp, Color(0xFF81C784)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = firstLetter,
                                            color = Color.White,
                                            fontSize = 42.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // EMAIL
                            Text(
                                text = if (userEmail.isNotEmpty()) userEmail else "Guest",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // BADGE
                            Box(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .background(
                                        color = Color(0xFFE8F5E8),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isGoogleLogin) "Google Account" else "Email Account",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // THEME TOGGLE CARD
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = MaterialTheme.shapes.medium,
                                clip = true
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFFE3F2FD),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                                    contentDescription = "Theme",
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Theme",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    if (isDarkMode) "Dark mode" else "Light mode",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Toggle switch
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = onThemeChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF2196F3),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFBDBDBD)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // LOGOUT CARD BUTTON
                    Card(
                        onClick = {
                            onLogout()
                            loginViewModel.logout { }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = MaterialTheme.shapes.medium,
                                clip = true
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFFFFEBEE),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    "Logout",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Sign out from current account",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // INFO TEXT
                Text(
                    text = "Health Tracker v1.0",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}
