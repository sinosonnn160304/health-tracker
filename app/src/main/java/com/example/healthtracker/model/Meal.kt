package com.example.healthtracker.model

import java.time.LocalDate

data class Meal(
    val id: String,
    val foodName: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val servingSize: Int, // in grams
    val date: LocalDate
)