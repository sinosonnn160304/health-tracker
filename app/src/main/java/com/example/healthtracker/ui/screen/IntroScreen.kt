package com.example.healthtracker.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthtracker.R

@Composable
fun IntroScreen(
    step: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip button
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Skip", color = Color.Gray, fontSize = 14.sp)
        }

        // Nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thanh progress phía trên ảnh - CHỈNH CAO HƠN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp), // Tăng từ 32dp lên 48dp để cao hơn
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Thanh segment 1
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(1f)
                        .background(
                            color = if (step >= 1) Color(0xFFFF9800) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                // Thanh segment 2
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(1f)
                        .background(
                            color = if (step >= 2) Color(0xFFFF9800) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                // Thanh segment 3
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(1f)
                        .background(
                            color = if (step >= 3) Color(0xFFFF9800) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            Image(
                painter = painterResource(
                    id = when (step) {
                        1 -> R.drawable.intro1
                        2 -> R.drawable.intro2
                        else -> R.drawable.intro3
                    }
                ),
                contentDescription = when (step) {
                    1 -> "Health Tracking"
                    2 -> "Health Analytics"
                    else -> "Goal Achievement"
                },
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(bottom = 40.dp),
                contentScale = ContentScale.Crop
            )

            // Tiêu đề
            Text(
                text = when (step) {
                    1 -> "Theo dõi sức khỏe toàn diện"
                    2 -> "Phân tích chi tiết"
                    else -> "Đạt mục tiêu sức khỏe"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mô tả
            Text(
                text = when (step) {
                    1 -> "Ghi lại các chỉ số sức khỏe quan trọng như nhịp tim, huyết áp, cân nặng và theo dõi tiến trình của bạn theo thời gian."
                    2 -> "Xem báo cáo chi tiết và biểu đồ trực quan để hiểu rõ hơn về tình trạng sức khỏe của bạn."
                    else -> "Đặt mục tiêu cá nhân, nhận lời nhắc và duy trì động lực trên hành trình chăm sóc sức khỏe."
                },
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Điều hướng cuối màn hình
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (step == 3) "Start" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (step == 3) "Start" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}