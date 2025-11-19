package com.example.healthtracker.ui.component.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.model.ActivityRecord
import java.util.UUID
import java.time.LocalDate
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityInputDialog(
    activity: ActivityRecord? = null,
    onDismiss: () -> Unit,
    onSave: (ActivityRecord) -> Unit
) {
    var activityName by remember { mutableStateOf(activity?.activityName ?: "") }
    var selectedType by remember { mutableStateOf(activity?.activityType ?: "Gym") }
    var durationMinutes by remember { mutableStateOf(activity?.durationMinutes?.toString() ?: "") }
    var caloriesBurned by remember { mutableStateOf(activity?.caloriesBurned?.toString() ?: "") }
    var selectedIntensity by remember { mutableStateOf(activity?.intensity ?: "Medium") }
    var expandedType by remember { mutableStateOf(false) }
    var expandedIntensity by remember { mutableStateOf(false) }

    // Activity types - limited to 3
    val activityTypes = listOf("Gym", "Running", "Yoga")
    val intensities = listOf("Low", "Medium", "High")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = if (activity == null) "Add Activity" else "Edit Activity",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Activity Name
                        OutlinedTextField(
                            value = activityName,
                            onValueChange = { activityName = it },
                            label = { Text("Activity Name") },
                            placeholder = { Text("e.g., Morning Workout") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF4CAF50),
                                unfocusedLabelColor = Color(0xFF757575),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    item {
                        // Activity Type Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedType,
                            onExpandedChange = { expandedType = !expandedType }
                        ) {
                            OutlinedTextField(
                                value = selectedType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Activity Type") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedLabelColor = Color(0xFF4CAF50),
                                    unfocusedLabelColor = Color(0xFF757575),
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = { expandedType = false }
                            ) {
                                activityTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(MaterialTheme.shapes.small)
                                                        .background(
                                                            when (type.lowercase()) {
                                                                "gym" -> Color(0xFFFFEBEE)
                                                                "running" -> Color(0xFFE8F5E8)
                                                                "yoga" -> Color(0xFFE3F2FD)
                                                                else -> Color(0xFFF3E5F5)
                                                            }
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = when (type.lowercase()) {
                                                            "gym" -> Icons.Default.FitnessCenter
                                                            "running" -> Icons.Default.DirectionsRun
                                                            "yoga" -> Icons.Default.Spa
                                                            else -> Icons.Default.SportsScore
                                                        },
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = when (type.lowercase()) {
                                                            "gym" -> Color(0xFFF44336)
                                                            "running" -> Color(0xFF4CAF50)
                                                            "yoga" -> Color(0xFF2196F3)
                                                            else -> Color(0xFF9C27B0)
                                                        }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(type, color = Color(0xFF424242))
                                            }
                                        },
                                        onClick = {
                                            selectedType = type
                                            expandedType = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Duration
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) durationMinutes = it
                            },
                            label = { Text("Duration (minutes)") },
                            placeholder = { Text("30") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF4CAF50),
                                unfocusedLabelColor = Color(0xFF757575),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    item {
                        // Calories Burned
                        OutlinedTextField(
                            value = caloriesBurned,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) caloriesBurned = it
                            },
                            label = { Text("Calories Burned") },
                            placeholder = { Text("200") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF4CAF50),
                                unfocusedLabelColor = Color(0xFF757575),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    item {
                        // Intensity Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedIntensity,
                            onExpandedChange = { expandedIntensity = !expandedIntensity }
                        ) {
                            OutlinedTextField(
                                value = selectedIntensity,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Intensity") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIntensity)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedLabelColor = Color(0xFF4CAF50),
                                    unfocusedLabelColor = Color(0xFF757575),
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedIntensity,
                                onDismissRequest = { expandedIntensity = false }
                            ) {
                                intensities.forEach { intensity ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                intensity,
                                                color = Color(0xFF424242),
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        onClick = {
                                            selectedIntensity = intensity
                                            expandedIntensity = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            "Cancel",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = {
                            if (activityName.isNotBlank() &&
                                durationMinutes.isNotBlank() &&
                                caloriesBurned.isNotBlank()) {
                                onSave(
                                    ActivityRecord(
                                        id = activity?.id ?: UUID.randomUUID().toString(),
                                        activityName = activityName,
                                        activityType = selectedType,
                                        durationMinutes = durationMinutes.toIntOrNull() ?: 0,
                                        caloriesBurned = caloriesBurned.toIntOrNull() ?: 0,
                                        intensity = selectedIntensity,
                                        date = activity?.date ?: LocalDate.now()
                                    )
                                )
                            }
                        },
                        modifier = Modifier.height(48.dp),
                        enabled = activityName.isNotBlank() &&
                                durationMinutes.isNotBlank() &&
                                caloriesBurned.isNotBlank()
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
