package com.example.healthtracker.ui.component.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.model.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealCard(
    meal: Meal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ),
        onClick = onEdit,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                when (meal.mealType.lowercase()) {
                                    "breakfast" -> Color(0xFFFFF3E0)
                                    "lunch" -> Color(0xFFE8F5E8)
                                    "dinner" -> Color(0xFFE3F2FD)
                                    "snack" -> Color(0xFFF3E5F5)
                                    else -> Color(0xFFE0E0E0)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (meal.mealType.lowercase()) {
                                "breakfast" -> Icons.Default.LightMode
                                "lunch" -> Icons.Default.Restaurant
                                "dinner" -> Icons.Default.RestaurantMenu
                                "snack" -> Icons.Default.Fastfood
                                else -> Icons.Default.Restaurant
                            },
                            contentDescription = meal.mealType,
                            tint = when (meal.mealType.lowercase()) {
                                "breakfast" -> Color(0xFFF57C00)
                                "lunch" -> Color(0xFF4CAF50)
                                "dinner" -> Color(0xFF2196F3)
                                "snack" -> Color(0xFF9C27B0)
                                else -> Color(0xFF757575)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = meal.mealType,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = meal.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NutritionBadge(
                        label = "Cal",
                        value = "${meal.calories}",
                        color = Color(0xFFF44336)
                    )
                    NutritionBadge(
                        label = "P",
                        value = "${meal.protein}g",
                        color = Color(0xFF4CAF50)
                    )
                    NutritionBadge(
                        label = "C",
                        value = "${meal.carbs}g",
                        color = Color(0xFFFF9800)
                    )
                    NutritionBadge(
                        label = "F",
                        value = "${meal.fat}g",
                        color = Color(0xFF2196F3)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (meal.servingSize > 0) {
                    Text(
                        text = "${meal.servingSize}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Meal?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = { Text("Are you sure you want to delete '${meal.foodName}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Delete", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

@Composable
private fun NutritionBadge(
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
