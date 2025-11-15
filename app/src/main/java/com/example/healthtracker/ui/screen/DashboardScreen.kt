package com.example.healthtracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.healthtracker.ui.component.dashboard.ProgressRing
import com.example.healthtracker.ui.component.dashboard.WeeklySummaryChart
import com.example.healthtracker.viewmodel.MealViewModel
import com.example.healthtracker.viewmodel.ActivityViewModel
import com.example.healthtracker.viewmodel.GoalViewModel
import com.example.healthtracker.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel,
    onNavigateToMeals: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToCharts: () -> Unit,
    onNavigateToHealthGoals: () -> Unit,
    onNavigateToHealthMetrics: () -> Unit,
    loginViewModel: LoginViewModel,
    onNavigateToUserProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val meals by mealViewModel.mealsForDate.collectAsState()
    val activities by activityViewModel.activitiesForDate.collectAsState()
    val goal by goalViewModel.currentGoal.collectAsState()
    val selectedDate by mealViewModel.selectedDate.collectAsState()
    val userPhotoUrl by loginViewModel.userPhotoUrl.collectAsState()

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = meals.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = meals.sumOf { it.fat.toDouble() }.toFloat()
    val caloriesBurned = activities.sumOf { it.caloriesBurned }
    val netCalories = totalCalories - caloriesBurned
    val calorieGoal = goal?.dailyCalorieGoal ?: 2000

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF4CAF50), CircleShape)
                            .clickable { onNavigateToUserProfile() }
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!userPhotoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = "User Profile",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Profile",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
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
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Date section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDate.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                        Row {
                            IconButton(
                                onClick = { mealViewModel.selectDate(selectedDate.minusDays(1)) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFE8F5E8),
                                    contentColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Icon(Icons.Default.ArrowBack, "Previous day")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { mealViewModel.selectDate(selectedDate.plusDays(1)) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFE8F5E8),
                                    contentColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Icon(Icons.Default.ArrowForward, "Next day")
                            }
                        }
                    }
                }

                // Calorie ring
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProgressRing(
                            current = netCalories,
                            goal = calorieGoal,
                            label = "Calories",
                            consumed = totalCalories,
                            burned = caloriesBurned
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hàng 1: Protein, Carbs, Fat - SỬ DỤNG CustomStatCard
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Protein - Màu pastel xanh lá
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                        ) {
                            CustomStatCard(
                                modifier = Modifier.fillMaxSize(),
                                title = "Protein",
                                value = "${totalProtein.toInt()}g",
                                goal = "${goal?.proteinGoal ?: 150}g",
                                color = Color(0xFF4CAF50),
                                backgroundColor = Color(0xFFE8F5E9),
                                icon = Icons.Default.FitnessCenter,
                                textColor = Color(0xFF000000)
                            )
                        }

                        // Carbs - Màu pastel cam
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                        ) {
                            CustomStatCard(
                                modifier = Modifier.fillMaxSize(),
                                title = "Carbs",
                                value = "${totalCarbs.toInt()}g",
                                goal = "${goal?.carbsGoal ?: 250}g",
                                color = Color(0xFFFF9800),
                                backgroundColor = Color(0xFFFFF3E0),
                                icon = Icons.Default.Eco,
                                textColor = Color(0xFF000000)
                            )
                        }

                        // Fat - Màu pastel đỏ
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                        ) {
                            CustomStatCard(
                                modifier = Modifier.fillMaxSize(),
                                title = "Fat",
                                value = "${totalFat.toInt()}g",
                                goal = "${goal?.fatGoal ?: 65}g",
                                color = Color(0xFFF44336),
                                backgroundColor = Color(0xFFFCE4EC),
                                icon = Icons.Default.Opacity,
                                textColor = Color(0xFF000000)
                            )
                        }
                    }

                    // Hàng 2: Add Meal, Add Activity, View Charts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Add Meal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                        ) {
                            ActionCard(
                                modifier = Modifier.fillMaxSize(),
                                title = "Add Meal",
                                icon = Icons.Default.Restaurant,
                                color = Color(0xFF2196F3),
                                backgroundColor = Color(0xFFE3F2FD),
                                onClick = onNavigateToMeals
                            )
                        }

                        // Add Activity
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                        ) {
                            ActionCard(
                                modifier = Modifier.fillMaxSize(),
                                title = "Add Activity",
                                icon = Icons.Default.DirectionsRun,
                                color = Color(0xFF9C27B0),
                                backgroundColor = Color(0xFFF3E5F5),
                                onClick = onNavigateToActivity
                            )
                        }

                        // View Charts
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                        ) {
                            ActionCard(
                                modifier = Modifier.fillMaxSize(),
                                title = "View Charts",
                                icon = Icons.Default.BarChart,
                                color = Color(0xFF607D8B),
                                backgroundColor = Color(0xFFECEFF1),
                                onClick = onNavigateToCharts
                            )
                        }
                    }
                }

                // Health Metrics
                Card(
                    onClick = { onNavigateToHealthMetrics() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MonitorHeart,
                                contentDescription = "Health Metrics",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Health Metrics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                            Text(
                                "Track your health measurements",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF757575)
                            )
                        }
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Navigate",
                            tint = Color(0xFF757575)
                        )
                    }
                }

                // Health Goals Button
                Card(
                    onClick = { onNavigateToHealthGoals() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Flag,
                                contentDescription = "Health Goals",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Health Goals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                            Text(
                                "View and manage your health targets",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF757575)
                            )
                        }
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Navigate",
                            tint = Color(0xFF757575)
                        )
                    }
                }

                // Weekly summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Weekly Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        WeeklySummaryChart(
                            mealViewModel = mealViewModel,
                            activityViewModel = activityViewModel,
                            goalViewModel = goalViewModel
                        )
                    }
                }

                // Meals summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Meals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                            TextButton(
                                onClick = onNavigateToMeals,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text("View All", fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (meals.isEmpty()) {
                            Text(
                                text = "No meals logged yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9E9E9E)
                            )
                        } else {
                            Text(
                                text = "${meals.size} meals logged • $totalCalories kcal",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }

                // Activities summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Activities",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                            TextButton(
                                onClick = onNavigateToActivity,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text("View All", fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (activities.isEmpty()) {
                            Text(
                                text = "No activities logged yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9E9E9E)
                            )
                        } else {
                            Text(
                                text = "${activities.size} activities • $caloriesBurned cal burned",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    backgroundColor: Color = Color.White,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CustomStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    goal: String,
    color: Color,
    backgroundColor: Color = Color.White,
    icon: ImageVector,
    textColor: Color = Color(0xFF000000)
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor) // SỬ DỤNG MÀU NỀN
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Value
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Goal
            Text(
                text = goal,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}