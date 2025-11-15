package com.example.healthtracker.ui.component.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.viewmodel.MealViewModel
import com.example.healthtracker.viewmodel.ActivityViewModel
import com.example.healthtracker.viewmodel.GoalViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeeklySummaryChart(
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel,
    modifier: Modifier = Modifier
) {
    val goal by goalViewModel.currentGoal.collectAsState()
    val calorieGoal = goal?.dailyCalorieGoal ?: 2000

    // Generate last 7 days
    val today = LocalDate.now()
    val weekDays = (6 downTo 0).map { today.minusDays(it.toLong()) }

    // Collect data for each day
    val weekData = weekDays.map { date ->
        val mealsForDate by mealViewModel.getMealsForDateRange(date, date).collectAsState(initial = emptyList())
        val activitiesForDate by activityViewModel.getActivitiesForDateRange(date, date).collectAsState(initial = emptyList())

        val totalCalories = mealsForDate.sumOf { it.calories }
        val caloriesBurned = activitiesForDate.sumOf { it.caloriesBurned }

        Triple(date, totalCalories, caloriesBurned)
    }

    // Calculate average calories
    val avgCalories = weekData.map { it.second }.average().toInt()

    // Calculate days on track
    val daysOnTrack = weekData.count {
        val netCalories = it.second - it.third
        netCalories in (calorieGoal - 200)..(calorieGoal + 200)
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                weekData.forEach { (date, calories, burned) ->
                    WeekDayBar(
                        date = date,
                        calories = calories,
                        calorieGoal = calorieGoal,
                        burned = burned
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Avg Calories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$avgCalories cal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Days on Track",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$daysOnTrack / 7",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDayBar(
    date: LocalDate,
    calories: Int,
    calorieGoal: Int,
    burned: Int
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val progress = (calories.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1.5f)
    val isToday = date == LocalDate.now()

    val barColor = when {
        progress < 0.8f -> Color(0xFF4CAF50) // Green - under goal
        progress < 1.0f -> Color(0xFFFFA726) // Orange - close to goal
        else -> Color(0xFFEF5350) // Red - over goal
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayName,
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )

        Column(modifier = Modifier.weight(1f)) {
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = barColor,
                trackColor = barColor.copy(alpha = 0.2f)
            )
            if (burned > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🔥 -$burned cal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$calories",
            modifier = Modifier.width(50.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}
