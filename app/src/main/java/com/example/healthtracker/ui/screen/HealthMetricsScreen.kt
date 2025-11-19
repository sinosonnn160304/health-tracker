package com.example.healthtracker.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthtracker.viewmodel.UserViewModel
import com.example.healthtracker.model.HealthMetrics
import com.example.healthtracker.ui.component.common.LoadingIndicator
import com.example.healthtracker.ui.component.common.ErrorState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMetricsScreen(
    userViewModel: UserViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val healthMetricsState by userViewModel.healthMetricsState.collectAsState()

    // Real-time data from meals and activities
    val totalCaloriesToday by userViewModel.totalCaloriesToday.collectAsState()
    val totalProteinToday by userViewModel.totalProteinToday.collectAsState()
    val totalCarbsToday by userViewModel.totalCarbsToday.collectAsState()
    val totalFatToday by userViewModel.totalFatToday.collectAsState()
    val totalActivityMinutesToday by userViewModel.totalActivityMinutesToday.collectAsState()
    val totalCaloriesBurnedToday by userViewModel.totalCaloriesBurnedToday.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Health Metrics",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
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
                .background(Color(0xFFF1F8E9))
                .padding(paddingValues)
        ) {
            when {
                healthMetricsState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
                    }
                }
                healthMetricsState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorState(message = healthMetricsState.error!!)
                    }
                }
                else -> {
                    healthMetricsState.metrics?.let { metrics ->
                        HealthMetricsContent(
                            metrics = metrics,
                            totalCaloriesToday = totalCaloriesToday,
                            totalProteinToday = totalProteinToday,
                            totalCarbsToday = totalCarbsToday,
                            totalFatToday = totalFatToday,
                            totalActivityMinutesToday = totalActivityMinutesToday,
                            totalCaloriesBurnedToday = totalCaloriesBurnedToday
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun HealthMetricsContent(
    metrics: HealthMetrics,
    totalCaloriesToday: Int,
    totalProteinToday: Float,
    totalCarbsToday: Float,
    totalFatToday: Float,
    totalActivityMinutesToday: Int,
    totalCaloriesBurnedToday: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.large
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Today's Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Real-time health tracking",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Physical Metrics Section
        item {
            Text(
                "Physical Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            MetricCard(
                label = "Cân nặng",
                value = "${metrics.weight} kg",
                color = Color(0xFF4CAF50)
            )
        }
        item {
            MetricCard(
                label = "Chiều cao",
                value = "${metrics.height} cm",
                color = Color(0xFF2196F3)
            )
        }
        item {
            val bmi = metrics.getBMI()
            val bmiStatus = when {
                bmi < 18.5 -> "Thiếu cân"
                bmi < 25 -> "Bình thường"
                bmi < 30 -> "Thừa cân"
                else -> "Béo phì"
            }
            MetricCard(
                label = "BMI",
                value = String.format("%.1f", bmi),
                subtitle = bmiStatus,
                color = Color(0xFFFF9800)
            )
        }
        item {
            val bmr = metrics.getBMR()
            MetricCard(
                label = "BMR",
                value = "${bmr.toInt()} kcal/day",
                subtitle = "Calo cơ bản mỗi ngày",
                color = Color(0xFF9C27B0)
            )
        }

        // Today's Nutrition Section
        item {
            Text(
                "Today's Nutrition (Real-time)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            MetricCard(
                label = "Tổng calo tiêu thụ hôm nay",
                value = "$totalCaloriesToday kcal",
                subtitle = "Updates automatically",
                color = Color(0xFFF44336)
            )
        }
        item {
            MetricCard(
                label = "Protein hôm nay",
                value = String.format("%.1f g", totalProteinToday),
                subtitle = "Updates automatically",
                color = Color(0xFF4CAF50)
            )
        }
        item {
            MetricCard(
                label = "Carbs hôm nay",
                value = String.format("%.1f g", totalCarbsToday),
                subtitle = "Updates automatically",
                color = Color(0xFFFFC107)
            )
        }
        item {
            MetricCard(
                label = "Fat hôm nay",
                value = String.format("%.1f g", totalFatToday),
                subtitle = "Updates automatically",
                color = Color(0xFF9C27B0)
            )
        }

        // Today's Activity Section
        item {
            Text(
                "Today's Activity (Real-time)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            MetricCard(
                label = "Thời gian hoạt động hôm nay",
                value = "$totalActivityMinutesToday min",
                subtitle = "Updates automatically",
                color = Color(0xFF00BCD4)
            )
        }
        item {
            MetricCard(
                label = "Calo đốt cháy hôm nay",
                value = "$totalCaloriesBurnedToday kcal",
                subtitle = "Updates automatically",
                color = Color(0xFFFF5722)
            )
        }

        // Net Calories
        item {
            val netCalories = totalCaloriesToday - totalCaloriesBurnedToday
            val netColor = if (netCalories > 0) Color(0xFFE91E63) else Color(0xFF4CAF50)
            MetricCard(
                label = "Calo thuần hôm nay",
                value = "$netCalories kcal",
                subtitle = "Tiêu thụ - Đốt cháy",
                color = netColor
            )
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    subtitle: String? = null,
    color: Color = Color(0xFF4CAF50)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Progress indicator (optional - for visual appeal)
            if (label == "BMI") {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = {
                        val bmi = value.replace(" kg", "").toFloatOrNull() ?: 0f
                        when {
                            bmi < 18.5 -> bmi / 18.5f
                            bmi < 25 -> (bmi - 18.5f) / 6.5f
                            bmi < 30 -> (bmi - 25f) / 5f
                            else -> 1f
                        }.coerceIn(0f, 1f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = color,
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        }
    }
}
