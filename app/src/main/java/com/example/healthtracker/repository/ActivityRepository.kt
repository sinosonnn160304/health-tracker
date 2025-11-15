package com.example.healthtracker.repository

import com.example.healthtracker.model.ActivityRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class ActivityRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getActivitiesCollection() =
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users")
                .document(userId)
                .collection("activities")
        }

    fun getAllActivities(): Flow<List<ActivityRecord>> = callbackFlow {
        val collection = getActivitiesCollection()
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

            val activities = snapshot?.documents?.mapNotNull { doc ->
                try {
                    ActivityRecord(
                        id = doc.id,
                        activityName = doc.getString("activityName") ?: "",
                        activityType = doc.getString("activityType") ?: "",
                        durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                        caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                        intensity = doc.getString("intensity") ?: "",
                        date = LocalDate.parse(doc.getString("date") ?: "")
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            trySend(activities)
        }

        awaitClose { listener.remove() }
    }

    fun getActivitiesByDate(date: LocalDate): Flow<List<ActivityRecord>> = callbackFlow {
        val collection = getActivitiesCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val dateStr = date.toString()
        val listener = collection
            .whereEqualTo("date", dateStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val activities = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ActivityRecord(
                            id = doc.id,
                            activityName = doc.getString("activityName") ?: "",
                            activityType = doc.getString("activityType") ?: "",
                            durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                            caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                            intensity = doc.getString("intensity") ?: "",
                            date = LocalDate.parse(doc.getString("date") ?: "")
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.sortedByDescending { it.durationMinutes } ?: emptyList()

                trySend(activities)
            }

        awaitClose { listener.remove() }
    }

    fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<ActivityRecord>> = callbackFlow {
        val collection = getActivitiesCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val startDateStr = startDate.toString()
        val endDateStr = endDate.toString()

        val listener = collection
            .whereGreaterThanOrEqualTo("date", startDateStr)
            .whereLessThanOrEqualTo("date", endDateStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val activities = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ActivityRecord(
                            id = doc.id,
                            activityName = doc.getString("activityName") ?: "",
                            activityType = doc.getString("activityType") ?: "",
                            durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                            caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                            intensity = doc.getString("intensity") ?: "",
                            date = LocalDate.parse(doc.getString("date") ?: "")
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.sortedByDescending { it.date } ?: emptyList()

                trySend(activities)
            }

        awaitClose { listener.remove() }
    }

    fun getActivityById(id: String): Flow<ActivityRecord?> = flow {
        try {
            val collection = getActivitiesCollection()
            if (collection == null) {
                emit(null)
                return@flow
            }

            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                val activity = ActivityRecord(
                    id = doc.id,
                    activityName = doc.getString("activityName") ?: "",
                    activityType = doc.getString("activityType") ?: "",
                    durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                    caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                    intensity = doc.getString("intensity") ?: "",
                    date = LocalDate.parse(doc.getString("date") ?: "")
                )
                emit(activity)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun getActivitiesByType(activityType: String): Flow<List<ActivityRecord>> = callbackFlow {
        val collection = getActivitiesCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection
            .whereEqualTo("activityType", activityType)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val activities = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ActivityRecord(
                            id = doc.id,
                            activityName = doc.getString("activityName") ?: "",
                            activityType = doc.getString("activityType") ?: "",
                            durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                            caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                            intensity = doc.getString("intensity") ?: "",
                            date = LocalDate.parse(doc.getString("date") ?: "")
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.sortedByDescending { it.date } ?: emptyList()

                trySend(activities)
            }

        awaitClose { listener.remove() }
    }

    suspend fun insertActivity(activity: ActivityRecord) {
        try {
            val collection = getActivitiesCollection() ?: return
            val activityData = hashMapOf(
                "activityName" to activity.activityName,
                "activityType" to activity.activityType,
                "durationMinutes" to activity.durationMinutes,
                "caloriesBurned" to activity.caloriesBurned,
                "intensity" to activity.intensity,
                "date" to activity.date.toString()
            )
            collection.document(activity.id).set(activityData).await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun updateActivity(activity: ActivityRecord) {
        try {
            val collection = getActivitiesCollection() ?: return
            val activityData = hashMapOf(
                "activityName" to activity.activityName,
                "activityType" to activity.activityType,
                "durationMinutes" to activity.durationMinutes,
                "caloriesBurned" to activity.caloriesBurned,
                "intensity" to activity.intensity,
                "date" to activity.date.toString()
            )
            collection.document(activity.id).set(activityData).await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun deleteActivity(activity: ActivityRecord) {
        try {
            val collection = getActivitiesCollection() ?: return
            collection.document(activity.id).delete().await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun deleteAllActivities() {
        try {
            val collection = getActivitiesCollection() ?: return
            val snapshot = collection.get().await()
            snapshot.documents.forEach { doc ->
                doc.reference.delete()
            }
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    fun getTotalCaloriesBurnedForDate(date: LocalDate): Flow<Int> = callbackFlow {
        val collection = getActivitiesCollection()
        if (collection == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val dateStr = date.toString()
        val listener = collection
            .whereEqualTo("date", dateStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }

                val total = snapshot?.documents?.sumOf {
                    it.getLong("caloriesBurned")?.toInt() ?: 0
                } ?: 0

                trySend(total)
            }

        awaitClose { listener.remove() }
    }

    fun getTotalDurationForDate(date: LocalDate): Flow<Int> = callbackFlow {
        val collection = getActivitiesCollection()
        if (collection == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val dateStr = date.toString()
        val listener = collection
            .whereEqualTo("date", dateStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }

                val total = snapshot?.documents?.sumOf {
                    it.getLong("durationMinutes")?.toInt() ?: 0
                } ?: 0

                trySend(total)
            }

        awaitClose { listener.remove() }
    }

    fun getWeeklyCaloriesBurned(): Flow<Map<LocalDate, Int>> = callbackFlow {
        val collection = getActivitiesCollection()
        if (collection == null) {
            trySend(emptyMap())
            close()
            return@callbackFlow
        }

        val today = LocalDate.now()
        val weekStart = today.minusDays(6)
        val weekStartStr = weekStart.toString()
        val todayStr = today.toString()

        val listener = collection
            .whereGreaterThanOrEqualTo("date", weekStartStr)
            .whereLessThanOrEqualTo("date", todayStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyMap())
                    return@addSnapshotListener
                }

                val activitiesByDate = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val date = LocalDate.parse(doc.getString("date") ?: "")
                        val calories = doc.getLong("caloriesBurned")?.toInt() ?: 0
                        date to calories
                    } catch (e: Exception) {
                        null
                    }
                }?.groupBy { it.first }
                    ?.mapValues { entry -> entry.value.sumOf { it.second } }
                    ?: emptyMap()

                val result = (0..6).associate { dayOffset ->
                    val date = weekStart.plusDays(dayOffset.toLong())
                    date to (activitiesByDate[date] ?: 0)
                }

                trySend(result)
            }

        awaitClose { listener.remove() }
    }
}
