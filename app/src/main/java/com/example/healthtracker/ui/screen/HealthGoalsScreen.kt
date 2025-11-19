package com.example.healthtracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.healthtracker.model.HealthGoal
import com.example.healthtracker.viewmodel.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthGoalsScreen(
    goalViewModel: GoalViewModel,
    onNavigateBack: () -> Unit
) {
    val goal by goalViewModel.currentGoal.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Health Goals",
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
        },
        floatingActionButton = {
            if (goal != null) {
                FloatingActionButton(
                    onClick = { showEditDialog = true },
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Goals")
                }
            }
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
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                goal?.let {
                    // Header Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
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
                                "Your Daily Goals",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Track and achieve your health targets",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Goals Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GoalCard(
                                title = "Calories",
                                value = "${it.dailyCalorieGoal}",
                                unit = "kcal",
                                icon = Icons.Outlined.LocalFireDepartment,
                                gradient = listOf(Color(0xFFFF9800), Color(0xFFF57C00))
                            )
                            GoalCard(
                                title = "Protein",
                                value = "${it.proteinGoal.toInt()}",
                                unit = "g",
                                icon = Icons.Outlined.Restaurant,
                                gradient = listOf(Color(0xFF4CAF50), Color(0xFF388E3C))
                            )
                            GoalCard(
                                title = "Water",
                                value = "${it.waterIntakeGoal}",
                                unit = "ml",
                                icon = Icons.Outlined.WaterDrop,
                                gradient = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GoalCard(
                                title = "Carbs",
                                value = "${it.carbsGoal.toInt()}",
                                unit = "g",
                                icon = Icons.Outlined.Restaurant,
                                gradient = listOf(Color(0xFFFFC107), Color(0xFFFFA000))
                            )
                            GoalCard(
                                title = "Fat",
                                value = "${it.fatGoal.toInt()}",
                                unit = "g",
                                icon = Icons.Outlined.Restaurant,
                                gradient = listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2))
                            )
                            GoalCard(
                                title = "Activity",
                                value = "${it.activityMinutesGoal}",
                                unit = "min",
                                icon = Icons.Outlined.FitnessCenter,
                                gradient = listOf(Color(0xFFE91E63), Color(0xFFC2185B))
                            )
                        }
                    }

                    // Weight Goal Card (Full Width)
                    GoalCard(
                        title = "Weight Goal",
                        value = "${it.weightGoal}",
                        unit = "kg",
                        icon = Icons.Outlined.MonitorWeight,
                        gradient = listOf(Color(0xFF607D8B), Color(0xFF455A64)),
                        modifier = Modifier.fillMaxWidth()
                    )

                } ?: run {
                    // Empty State
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = MaterialTheme.shapes.large
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = "No Goals",
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF9E9E9E)
                            )
                            Text(
                                "No Health Goals Set",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Start your health journey by setting your personal goals",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { showEditDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("Set Your Goals", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditGoalDialog(
            currentGoal = goal ?: HealthGoal(
                id = "",
                dailyCalorieGoal = 2000,
                proteinGoal = 150f,
                carbsGoal = 250f,
                fatGoal = 65f,
                waterIntakeGoal = 2000,
                weightGoal = 60f,
                activityMinutesGoal = 30,
                isActive = true
            ),
            onDismiss = { showEditDialog = false },
            onSave = { updatedGoal ->
                goalViewModel.updateGoal(updatedGoal)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun GoalCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(colors = gradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun EditGoalDialog(
    currentGoal: HealthGoal,
    onDismiss: () -> Unit,
    onSave: (HealthGoal) -> Unit
) {
    val dialogState = remember { DialogState(currentGoal) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    "Edit Health Goals",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // SỬ DỤNG LAZYCOLUMN
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        GoalTextField(
                            value = dialogState.calories,
                            onValueChange = { dialogState.calories = it },
                            label = "Calories (kcal)"
                        )
                    }
                    item {
                        GoalTextField(
                            value = dialogState.protein,
                            onValueChange = { dialogState.protein = it },
                            label = "Protein (g)"
                        )
                    }
                    item {
                        GoalTextField(
                            value = dialogState.carbs,
                            onValueChange = { dialogState.carbs = it },
                            label = "Carbs (g)"
                        )
                    }
                    item {
                        GoalTextField(
                            value = dialogState.fat,
                            onValueChange = { dialogState.fat = it },
                            label = "Fat (g)"
                        )
                    }
                    item {
                        GoalTextField(
                            value = dialogState.water,
                            onValueChange = { dialogState.water = it },
                            label = "Water (ml)"
                        )
                    }
                    item {
                        GoalTextField(
                            value = dialogState.weight,
                            onValueChange = { dialogState.weight = it },
                            label = "Weight (kg)"
                        )
                    }
                    item {
                        GoalTextField(
                            value = dialogState.activity,
                            onValueChange = { dialogState.activity = it },
                            label = "Activity (min/day)"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = {
                            onSave(dialogState.toHealthGoal(currentGoal))
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            "Save",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAF50),
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = Color(0xFF4CAF50),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

class DialogState(goal: HealthGoal) {
    var calories by mutableStateOf(goal.dailyCalorieGoal.toString())
    var protein by mutableStateOf(goal.proteinGoal.toString())
    var carbs by mutableStateOf(goal.carbsGoal.toString())
    var fat by mutableStateOf(goal.fatGoal.toString())
    var water by mutableStateOf(goal.waterIntakeGoal.toString())
    var weight by mutableStateOf(goal.weightGoal.toString())
    var activity by mutableStateOf(goal.activityMinutesGoal.toString())

    fun toHealthGoal(original: HealthGoal): HealthGoal {
        return original.copy(
            dailyCalorieGoal = calories.toIntOrNull() ?: original.dailyCalorieGoal,
            proteinGoal = protein.toFloatOrNull() ?: original.proteinGoal,
            carbsGoal = carbs.toFloatOrNull() ?: original.carbsGoal,
            fatGoal = fat.toFloatOrNull() ?: original.fatGoal,
            waterIntakeGoal = water.toIntOrNull() ?: original.waterIntakeGoal,
            weightGoal = weight.toFloatOrNull() ?: original.weightGoal,
            activityMinutesGoal = activity.toIntOrNull() ?: original.activityMinutesGoal
        )
    }
}
