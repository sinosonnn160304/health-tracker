package com.example.healthtracker.ui.component.activity

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
import com.example.healthtracker.model.ActivityRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCard(
    activity: ActivityRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.medium),
        onClick = onEdit,
        color = Color.White,
        shape = MaterialTheme.shapes.medium
    )  {
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
                                when (activity.activityType.lowercase()) {
                                    "gym" -> Color(0xFFFFEBEE)
                                    "running" -> Color(0xFFE8F5E8)
                                    "yoga" -> Color(0xFFE3F2FD)
                                    else -> Color(0xFFF3E5F5)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (activity.activityType.lowercase()) {
                                "gym" -> Icons.Default.FitnessCenter
                                "running" -> Icons.Default.DirectionsRun
                                "yoga" -> Icons.Default.Spa
                                else -> Icons.Default.SportsScore
                            },
                            contentDescription = activity.activityType,
                            tint = when (activity.activityType.lowercase()) {
                                "gym" -> Color(0xFFF44336)
                                "running" -> Color(0xFF4CAF50)
                                "yoga" -> Color(0xFF2196F3)
                                else -> Color(0xFF9C27B0)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = activity.activityType,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activity.activityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActivityBadge(
                        label = "Time",
                        value = "${activity.durationMinutes}m",
                        color = Color(0xFF2196F3)
                    )
                    ActivityBadge(
                        label = "Cal",
                        value = "${activity.caloriesBurned}",
                        color = Color(0xFFF44336)
                    )
                    ActivityBadge(
                        label = "Intensity",
                        value = activity.intensity,
                        color = when (activity.intensity.lowercase()) {
                            "high" -> Color(0xFFD32F2F)
                            "medium" -> Color(0xFFF57C00)
                            else -> Color(0xFF388E3C)
                        }
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
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Activity?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
            },
            text = { Text("Are you sure you want to delete '${activity.activityName}'?") },
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
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

@Composable
private fun ActivityBadge(
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
            color = Color(0xFF424242)
        )
    }
}