package com.example.healthtracker.util.calculators

object BMIUtils {
    fun calculateBMI(weight: Float, height: Float): Float {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }
}
