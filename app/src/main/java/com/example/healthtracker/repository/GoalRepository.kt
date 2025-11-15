package com.example.healthtracker.repository

import com.example.healthtracker.model.HealthGoal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GoalRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getGoalsCollection() =
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users")
                .document(userId)
                .collection("goals")
        }

    suspend fun ensureDefaultGoalExists() {
        try {
            val collection = getGoalsCollection() ?: return
            val snapshot = collection.get().await()
            if (snapshot.isEmpty) {
                // Create default goal if none exists
                val defaultGoal = HealthGoal(
                    id = UUID.randomUUID().toString(),
                    dailyCalorieGoal = 2000,
                    proteinGoal = 150f,
                    carbsGoal = 250f,
                    fatGoal = 65f,
                    waterIntakeGoal = 2000,
                    weightGoal = 70f,
                    activityMinutesGoal = 30,
                    isActive = true
                )
                insertGoal(defaultGoal)
            }
        } catch (e: Exception) {
            // If error, default goal will be created on next attempt
        }
    }

    fun getAllGoals(): Flow<List<HealthGoal>> = callbackFlow {
        val collection = getGoalsCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val goals = snapshot?.documents?.mapNotNull { doc ->
                try {
                    HealthGoal(
                        id = doc.id,
                        dailyCalorieGoal = doc.getLong("dailyCalorieGoal")?.toInt() ?: 2000,
                        proteinGoal = doc.getDouble("proteinGoal")?.toFloat() ?: 150f,
                        carbsGoal = doc.getDouble("carbsGoal")?.toFloat() ?: 250f,
                        fatGoal = doc.getDouble("fatGoal")?.toFloat() ?: 65f,
                        waterIntakeGoal = doc.getLong("waterIntakeGoal")?.toInt() ?: 2000,
                        weightGoal = doc.getDouble("weightGoal")?.toFloat() ?: 70f,
                        activityMinutesGoal = doc.getLong("activityMinutesGoal")?.toInt() ?: 30,
                        isActive = doc.getBoolean("isActive") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            trySend(goals)
        }

        awaitClose { listener.remove() }
    }

    fun getCurrentGoal(): Flow<HealthGoal?> = callbackFlow {
        val collection = getGoalsCollection()
        if (collection == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = collection
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val activeGoal = snapshot?.documents?.firstOrNull()?.let { doc ->
                    try {
                        HealthGoal(
                            id = doc.id,
                            dailyCalorieGoal = doc.getLong("dailyCalorieGoal")?.toInt() ?: 2000,
                            proteinGoal = doc.getDouble("proteinGoal")?.toFloat() ?: 150f,
                            carbsGoal = doc.getDouble("carbsGoal")?.toFloat() ?: 250f,
                            fatGoal = doc.getDouble("fatGoal")?.toFloat() ?: 65f,
                            waterIntakeGoal = doc.getLong("waterIntakeGoal")?.toInt() ?: 2000,
                            weightGoal = doc.getDouble("weightGoal")?.toFloat() ?: 70f,
                            activityMinutesGoal = doc.getLong("activityMinutesGoal")?.toInt() ?: 30,
                            isActive = true
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                trySend(activeGoal)
            }

        awaitClose { listener.remove() }
    }

    fun getGoalById(id: String): Flow<HealthGoal?> = flow {
        try {
            val collection = getGoalsCollection()
            if (collection == null) {
                emit(null)
                return@flow
            }

            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                val goal = HealthGoal(
                    id = doc.id,
                    dailyCalorieGoal = doc.getLong("dailyCalorieGoal")?.toInt() ?: 2000,
                    proteinGoal = doc.getDouble("proteinGoal")?.toFloat() ?: 150f,
                    carbsGoal = doc.getDouble("carbsGoal")?.toFloat() ?: 250f,
                    fatGoal = doc.getDouble("fatGoal")?.toFloat() ?: 65f,
                    waterIntakeGoal = doc.getLong("waterIntakeGoal")?.toInt() ?: 2000,
                    weightGoal = doc.getDouble("weightGoal")?.toFloat() ?: 70f,
                    activityMinutesGoal = doc.getLong("activityMinutesGoal")?.toInt() ?: 30,
                    isActive = doc.getBoolean("isActive") ?: false
                )
                emit(goal)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    suspend fun insertGoal(goal: HealthGoal) {
        try {
            val collection = getGoalsCollection() ?: return

            // If new goal is active, deactivate all other goals first
            if (goal.isActive) {
                val snapshot = collection.get().await()
                snapshot.documents.forEach { doc ->
                    doc.reference.update("isActive", false).await()
                }
            }

            val goalData = hashMapOf(
                "dailyCalorieGoal" to goal.dailyCalorieGoal,
                "proteinGoal" to goal.proteinGoal,
                "carbsGoal" to goal.carbsGoal,
                "fatGoal" to goal.fatGoal,
                "waterIntakeGoal" to goal.waterIntakeGoal,
                "weightGoal" to goal.weightGoal,
                "activityMinutesGoal" to goal.activityMinutesGoal,
                "isActive" to goal.isActive
            )

            collection.document(goal.id).set(goalData).await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun updateGoal(goal: HealthGoal) {
        try {
            val collection = getGoalsCollection() ?: return

            // If updated goal is active, deactivate all other goals first
            if (goal.isActive) {
                val snapshot = collection.get().await()
                snapshot.documents.forEach { doc ->
                    if (doc.id != goal.id) {
                        doc.reference.update("isActive", false).await()
                    }
                }
            }

            val goalData = hashMapOf(
                "dailyCalorieGoal" to goal.dailyCalorieGoal,
                "proteinGoal" to goal.proteinGoal,
                "carbsGoal" to goal.carbsGoal,
                "fatGoal" to goal.fatGoal,
                "waterIntakeGoal" to goal.waterIntakeGoal,
                "weightGoal" to goal.weightGoal,
                "activityMinutesGoal" to goal.activityMinutesGoal,
                "isActive" to goal.isActive
            )

            collection.document(goal.id).set(goalData).await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun deleteGoal(goal: HealthGoal) {
        try {
            val collection = getGoalsCollection() ?: return
            collection.document(goal.id).delete().await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun setActiveGoal(goalId: String) {
        try {
            val collection = getGoalsCollection() ?: return

            // Deactivate all goals first
            val snapshot = collection.get().await()
            snapshot.documents.forEach { doc ->
                doc.reference.update("isActive", false).await()
            }

            // Activate the specified goal
            collection.document(goalId).update("isActive", true).await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun deleteAllGoals() {
        try {
            val collection = getGoalsCollection() ?: return
            val snapshot = collection.get().await()
            snapshot.documents.forEach { doc ->
                doc.reference.delete()
            }
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }
}
