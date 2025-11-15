package com.example.healthtracker.util.calculators

object BMRCalculator {
    fun calculateBMR(weight: Float, height: Float, age: Int, gender: String): Float {
        return if (gender.lowercase() == "male") {
            88.36f + (13.4f * weight) + (4.8f * height) - (5.7f * age)
        } else {
            447.6f + (9.2f * weight) + (3.1f * height) - (4.3f * age)
        }
    }
}
