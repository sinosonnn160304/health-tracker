package com.example.healthtracker.model

import java.time.LocalDate
data class ActivityRecord(
    val id: String,
    val activityName: String,
    val activityType: String, // Running, Walking, Cycling, Swimming, Gym, Yoga, etc.
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val intensity: String, // Low, Medium, High
    val date: LocalDate
)