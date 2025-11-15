package com.example.healthtracker.model

import com.example.healthtracker.util.calculators.BMRCalculator
import com.example.healthtracker.util.calculators.BMIUtils

data class HealthMetrics(
    val weight: Float, // kg
    val height: Float, // cm
    val age: Int,
    val gender: String, // "male" / "female"
    val totalCalories: Int
) {
    fun getBMI(): Float = BMIUtils.calculateBMI(weight, height)
    fun getBMR(): Float = BMRCalculator.calculateBMR(weight, height, age, gender)
}


