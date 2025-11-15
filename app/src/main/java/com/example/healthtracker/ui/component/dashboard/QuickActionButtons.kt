package com.example.healthtracker.ui.component.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickActionButtons(
    onAddMeal: () -> Unit,
    onAddActivity: () -> Unit,
    onViewCharts: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Restaurant,
            label = "Add Meal",
            onClick = onAddMeal,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FitnessCenter,
            label = "Add Activity",
            onClick = onAddActivity,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.ShowChart,
            label = "View Charts",
            onClick = onViewCharts,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}