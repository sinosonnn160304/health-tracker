package com.example.healthtracker.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.healthtracker.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen", "ConfigurationScreenWidthHeight")
@Composable
fun LaunchScreen(
    onNavigateToSplash: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Delay rồi điều hướng
    LaunchedEffect(Unit) {
        delay(3000)

        if (currentUser != null) {
            // Đã đăng nhập từ trước → vào thẳng Main/UserProfile
            onNavigateToMain()
        } else {
            // Chưa đăng nhập → vào SplashScreen
            onNavigateToSplash()
        }
    }

    // Logo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001A00)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.background_image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size((screenHeight * 0.30f))
                .clip(CircleShape)
        )
    }
}