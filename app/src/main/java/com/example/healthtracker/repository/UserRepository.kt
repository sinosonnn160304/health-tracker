package com.example.healthtracker.repository

import com.example.healthtracker.model.HealthMetrics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Fetch HealthMetrics của user hiện tại
    suspend fun getHealthMetrics(): HealthMetrics {
        val userId = auth.currentUser?.uid
            ?: throw Exception("User not logged in")

        return try {
            val docRef = firestore.collection("users").document(userId)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                // Nếu user chưa có dữ liệu trong Firestore → trả về fake data
                getFakeHealthMetrics()
            } else {
                val weight = snapshot.getDouble("weight")?.toFloat() ?: 0f
                val height = snapshot.getDouble("height")?.toFloat() ?: 0f
                val age = snapshot.getLong("age")?.toInt() ?: 0
                val gender = snapshot.getString("gender") ?: "male"
                val totalCalories = snapshot.getLong("totalCalories")?.toInt() ?: 0

                HealthMetrics(
                    weight = weight,
                    height = height,
                    age = age,
                    gender = gender,
                    totalCalories = totalCalories
                )
            }
        } catch (e: Exception) {
            // Nếu lỗi mạng hoặc Firestore fail → cũng dùng fake data
            getFakeHealthMetrics()
        }
    }

    // Fake data để test UI khi chưa có dữ liệu thật
    private fun getFakeHealthMetrics(): HealthMetrics {
        return HealthMetrics(
            weight = 65f,
            height = 170f,
            age = 25,
            gender = "male",
            totalCalories = 2200
        )
    }
}
