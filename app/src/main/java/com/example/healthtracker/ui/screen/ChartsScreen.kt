package com.example.healthtracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.viewmodel.MealViewModel
import com.example.healthtracker.viewmodel.ActivityViewModel
import com.example.healthtracker.viewmodel.GoalViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly", "Monthly", "Yearly")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Statistics & Charts",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Back",
                            tint = Color(0xFF2E7D32)
                        )
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
            ) {
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF2E7D32),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            selectedContentColor = Color(0xFF2E7D32),
                            unselectedContentColor = Color(0xFF757575)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    when (selectedTab) {
                        0 -> WeeklyCharts(mealViewModel, activityViewModel, goalViewModel)
                        1 -> MonthlyCharts(mealViewModel, activityViewModel, goalViewModel)
                        2 -> YearlyCharts(mealViewModel, activityViewModel, goalViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyCharts(
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel
) {
    val goal by goalViewModel.currentGoal.collectAsState()

    // Generate last 7 days and collect real data
    val today = LocalDate.now()
    val weekStart = today.minusDays(6)

    val weekData = (0..6).map { dayOffset ->
        val date = weekStart.plusDays(dayOffset.toLong())
        val mealsForDate by mealViewModel.getMealsForDateRange(date, date).collectAsState(initial = emptyList())
        val activitiesForDate by activityViewModel.getActivitiesForDateRange(date, date).collectAsState(initial = emptyList())

        val totalCalories = mealsForDate.sumOf { it.calories }
        val totalProtein = mealsForDate.sumOf { it.protein.toDouble() }.toFloat()
        val totalCarbs = mealsForDate.sumOf { it.carbs.toDouble() }.toFloat()
        val totalFat = mealsForDate.sumOf { it.fat.toDouble() }.toFloat()
        val caloriesBurned = activitiesForDate.sumOf { it.caloriesBurned }
        val activityMinutes = activitiesForDate.sumOf { it.durationMinutes }

        mapOf(
            "date" to date,
            "calories" to totalCalories,
            "protein" to totalProtein,
            "carbs" to totalCarbs,
            "fat" to totalFat,
            "burned" to caloriesBurned,
            "minutes" to activityMinutes,
            "activityCount" to activitiesForDate.size
        )
    }

    val calorieData = weekData.map { it["calories"] as Int }
    val days = weekData.map {
        (it["date"] as LocalDate).dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
    }

    // Weekly totals for macros
    val weeklyProtein = weekData.sumOf { (it["protein"] as Float).toDouble() }.toFloat()
    val weeklyCarbs = weekData.sumOf { (it["carbs"] as Float).toDouble() }.toFloat()
    val weeklyFat = weekData.sumOf { (it["fat"] as Float).toDouble() }.toFloat()

    // Weekly activity totals
    val totalWorkouts = weekData.sumOf { it["activityCount"] as Int }
    val totalMinutes = weekData.sumOf { it["minutes"] as Int }
    val totalBurned = weekData.sumOf { it["burned"] as Int }

    // Calorie trend card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Calorie Trend (Last 7 Days)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                calorieData.forEachIndexed { index, calories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = days[index],
                            modifier = Modifier.width(40.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Medium
                        )
                        LinearProgressIndicator(
                            progress = (calories / 2500f).coerceIn(0f, 1f),
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .clip(MaterialTheme.shapes.small),
                            color = if (calories > (goal?.dailyCalorieGoal ?: 2000))
                                Color(0xFFF44336)
                            else
                                Color(0xFF4CAF50),
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$calories cal",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(60.dp),
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Average",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "${calorieData.average().toInt()} cal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Daily Goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "${goal?.dailyCalorieGoal ?: 2000} cal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }

    // Macro distribution card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFFE8F5E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DonutLarge,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Macro Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Weekly macro totals
            val total = weeklyProtein + weeklyCarbs + weeklyFat

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (total > 0) {
                    MacroBar("Protein", weeklyProtein, total, Color(0xFF4CAF50))
                    MacroBar("Carbs", weeklyCarbs, total, Color(0xFFFF9800))
                    MacroBar("Fat", weeklyFat, total, Color(0xFFF44336))
                } else {
                    Text(
                        text = "No macro data for this week",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }

    // Activity summary card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Activity Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem("Workouts", "$totalWorkouts", Icons.Default.DirectionsRun, Color(0xFF4CAF50))
                SummaryItem("Minutes", "$totalMinutes", Icons.Default.Schedule, Color(0xFF2196F3))
                SummaryItem("Burned", "$totalBurned", Icons.Default.Whatshot, Color(0xFFF44336))
            }
        }
    }
}

@Composable
private fun MonthlyCharts(
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Monthly Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming soon!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
private fun YearlyCharts(
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Yearly Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming soon!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
private fun MacroBar(
    label: String,
    value: Float,
    total: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242)
            )
            Text(
                text = "${value.toInt()}g (${(value / total * 100).toInt()}%)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = value / total,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(MaterialTheme.shapes.small),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
    }
}