package com.example.healthtracker.model.state

import com.example.healthtracker.model.HealthMetrics

data class HealthMetricsState(
    val isLoading: Boolean = false,
    val metrics: HealthMetrics? = null,
    val error: String? = null
)
