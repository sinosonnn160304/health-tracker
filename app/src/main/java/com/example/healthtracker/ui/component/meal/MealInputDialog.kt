package com.example.healthtracker.ui.component.meal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthtracker.model.Meal
import java.time.LocalDate
import java.util.UUID
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealInputDialog(
    meal: Meal? = null,
    onDismiss: () -> Unit,
    onSave: (Meal) -> Unit
) {
    var foodName by remember { mutableStateOf(meal?.foodName ?: "") }
    var mealType by remember { mutableStateOf(meal?.mealType ?: "Breakfast") }
    var calories by remember { mutableStateOf(meal?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(meal?.protein?.toString() ?: "") }
    var carbs by remember { mutableStateOf(meal?.carbs?.toString() ?: "") }
    var fat by remember { mutableStateOf(meal?.fat?.toString() ?: "") }
    var servingSize by remember { mutableStateOf(meal?.servingSize?.toString() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = MaterialTheme.shapes.large
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(36.dp)
            ) {
                Text(
                    if (meal == null) "Add Meal" else "Edit Meal",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        // Food name
                        OutlinedTextField(
                            value = foodName,
                            onValueChange = { foodName = it },
                            label = {
                                Text(
                                    "Food Name",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Restaurant,
                                    null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF4CAF50),
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    item {
                        // Meal type dropdown
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = mealType,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        "Meal Type",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp)
                                    .menuAnchor(),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedLabelColor = Color(0xFF4CAF50),
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                mealTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                type,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        onClick = {
                                            mealType = type
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Calories
                        OutlinedTextField(
                            value = calories,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                    calories = it
                                }
                            },
                            label = {
                                Text(
                                    "Calories",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Whatshot,
                                    null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF4CAF50),
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    item {
                        // Macros row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = protein,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                        protein = it
                                    }
                                },
                                label = {
                                    Text(
                                        "Protein (g)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(68.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedLabelColor = Color(0xFF4CAF50),
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            OutlinedTextField(
                                value = carbs,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                        carbs = it
                                    }
                                },
                                label = {
                                    Text(
                                        "Carbs (g)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(68.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedLabelColor = Color(0xFF4CAF50),
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            OutlinedTextField(
                                value = fat,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                        fat = it
                                    }
                                },
                                label = {
                                    Text(
                                        "Fat (g)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(68.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedLabelColor = Color(0xFF4CAF50),
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    item {
                        // Serving size
                        OutlinedTextField(
                            value = servingSize,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                    servingSize = it
                                }
                            },
                            label = {
                                Text(
                                    "Serving Size (g)",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MonitorWeight,
                                    null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF4CAF50),
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(52.dp)
                            .width(120.dp)
                    ) {
                        Text(
                            "Cancel",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        onClick = {
                            if (foodName.isNotBlank() && calories.isNotBlank()) {
                                val newMeal = Meal(
                                    id = meal?.id ?: UUID.randomUUID().toString(),
                                    foodName = foodName,
                                    mealType = mealType,
                                    calories = calories.toIntOrNull() ?: 0,
                                    protein = protein.toFloatOrNull() ?: 0f,
                                    carbs = carbs.toFloatOrNull() ?: 0f,
                                    fat = fat.toFloatOrNull() ?: 0f,
                                    servingSize = servingSize.toIntOrNull() ?: 0,
                                    date = meal?.date ?: LocalDate.now()
                                )
                                onSave(newMeal)
                            }
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .width(120.dp),
                        enabled = foodName.isNotBlank() && calories.isNotBlank()
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
