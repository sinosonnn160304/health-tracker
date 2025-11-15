package com.example.healthtracker.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthtracker.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@SuppressLint("UnusedBoxWithConstraintsScope", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,   // callback điều hướng đến LoginScreen
    onNavigateToSignup: () -> Unit // callback điều hướng đến SignupScreen
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Auto slide ảnh
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % 3
            pagerState.animateScrollToPage(nextPage)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = ((screenWidth.value * 0.08f).coerceIn(16f, 32f)).dp)
    ) {
        val maxH = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height((maxH.value * 0.05f).dp))

            // ===== HEADER =====
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "welcome to",
                    fontSize = ((maxH.value * 0.02f).coerceIn(14f, 18f)).sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "myhealthtracker",
                    fontSize = ((maxH.value * 0.04f).coerceIn(24f, 36f)).sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height((maxH.value * 0.03f).dp))

            // ===== SLIDER ẢNH =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(((maxH.value * 0.4f).coerceIn(200f, 320f)).dp)
            ) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    val imageRes = when (page) {
                        0 -> R.drawable.onboarding_1
                        1 -> R.drawable.onboarding_2
                        else -> R.drawable.onboarding_3
                    }
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Onboarding image ${page + 1}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // ===== CHẤM TRANG =====
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    Color(0xFF4CAF50)
                                else
                                    Color.Gray.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height((maxH.value * 0.05f).dp))

            // ===== NÚT ĐI ĐẾN LOGIN / SIGNUP =====
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNavigateToSignup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((maxH.value * 0.07f).coerceIn(48f, 60f)).dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Sign Up For Free",
                        fontSize = ((maxH.value * 0.02f).coerceIn(14f, 18f)).sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((maxH.value * 0.07f).coerceIn(48f, 60f)).dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = "Log In",
                        fontSize = ((maxH.value * 0.02f).coerceIn(14f, 18f)).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height((maxH.value * 0.05f).dp))
        }
    }
}