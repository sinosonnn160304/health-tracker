package com.example.healthtracker.model

data class HealthGoal(
    val id: String,
    val dailyCalorieGoal: Int,
    val proteinGoal: Float, // in grams
    val carbsGoal: Float, // in grams
    val fatGoal: Float, // in grams
    val waterIntakeGoal: Int = 2000, // in ml
    val weightGoal: Float = 0f, // in kg
    val activityMinutesGoal: Int = 30, // daily activity minutes
    val isActive: Boolean = true
)