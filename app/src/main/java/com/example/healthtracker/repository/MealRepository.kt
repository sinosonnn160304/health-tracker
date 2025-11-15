package com.example.healthtracker.repository

import android.util.Log
import com.example.healthtracker.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class MealRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "MealRepository"
    }

    private fun getMealsCollection() =
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("users")
                .document(userId)
                .collection("meals")
        }

    fun getAllMeals(): Flow<List<Meal>> = callbackFlow {
        val collection = getMealsCollection()
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

            val meals = snapshot?.documents?.mapNotNull { doc ->
                try {
                    Meal(
                        id = doc.id,
                        foodName = doc.getString("foodName") ?: "",
                        mealType = doc.getString("mealType") ?: "",
                        calories = doc.getLong("calories")?.toInt() ?: 0,
                        protein = doc.getDouble("protein")?.toFloat() ?: 0f,
                        carbs = doc.getDouble("carbs")?.toFloat() ?: 0f,
                        fat = doc.getDouble("fat")?.toFloat() ?: 0f,
                        servingSize = doc.getLong("servingSize")?.toInt() ?: 0,
                        date = LocalDate.parse(doc.getString("date") ?: "")
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            trySend(meals)
        }

        awaitClose { listener.remove() }
    }

    fun getMealsByDate(date: LocalDate): Flow<List<Meal>> = callbackFlow {
        val collection = getMealsCollection()
        if (collection == null) {
            Log.e(TAG, "getMealsByDate: Collection is null - user not logged in!")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val dateStr = date.toString()
        Log.d(TAG, "getMealsByDate: Listening for meals on date: $dateStr")
        Log.d(TAG, "getMealsByDate: Path: users/${auth.currentUser?.uid}/meals")

        val listener = collection
            .whereEqualTo("date", dateStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "getMealsByDate: Error listening to meals", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                Log.d(TAG, "getMealsByDate: Snapshot received, document count: ${snapshot?.size() ?: 0}")

                val meals = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Log.d(TAG, "getMealsByDate: Processing document: ${doc.id}, data: ${doc.data}")
                        Meal(
                            id = doc.id,
                            foodName = doc.getString("foodName") ?: "",
                            mealType = doc.getString("mealType") ?: "",
                            calories = doc.getLong("calories")?.toInt() ?: 0,
                            protein = doc.getDouble("protein")?.toFloat() ?: 0f,
                            carbs = doc.getDouble("carbs")?.toFloat() ?: 0f,
                            fat = doc.getDouble("fat")?.toFloat() ?: 0f,
                            servingSize = doc.getLong("servingSize")?.toInt() ?: 0,
                            date = LocalDate.parse(doc.getString("date") ?: "")
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "getMealsByDate: Error parsing meal document ${doc.id}", e)
                        null
                    }
                }?.sortedBy { it.mealType } ?: emptyList()

                Log.d(TAG, "getMealsByDate: Sending ${meals.size} meals to UI")
                trySend(meals)
            }

        awaitClose { listener.remove() }
    }

    fun getMealsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Meal>> = callbackFlow {
        val collection = getMealsCollection()
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

                val meals = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Meal(
                            id = doc.id,
                            foodName = doc.getString("foodName") ?: "",
                            mealType = doc.getString("mealType") ?: "",
                            calories = doc.getLong("calories")?.toInt() ?: 0,
                            protein = doc.getDouble("protein")?.toFloat() ?: 0f,
                            carbs = doc.getDouble("carbs")?.toFloat() ?: 0f,
                            fat = doc.getDouble("fat")?.toFloat() ?: 0f,
                            servingSize = doc.getLong("servingSize")?.toInt() ?: 0,
                            date = LocalDate.parse(doc.getString("date") ?: "")
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.sortedByDescending { it.date } ?: emptyList()

                trySend(meals)
            }

        awaitClose { listener.remove() }
    }

    fun getMealById(id: String): Flow<Meal?> = flow {
        try {
            val collection = getMealsCollection()
            if (collection == null) {
                emit(null)
                return@flow
            }

            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                val meal = Meal(
                    id = doc.id,
                    foodName = doc.getString("foodName") ?: "",
                    mealType = doc.getString("mealType") ?: "",
                    calories = doc.getLong("calories")?.toInt() ?: 0,
                    protein = doc.getDouble("protein")?.toFloat() ?: 0f,
                    carbs = doc.getDouble("carbs")?.toFloat() ?: 0f,
                    fat = doc.getDouble("fat")?.toFloat() ?: 0f,
                    servingSize = doc.getLong("servingSize")?.toInt() ?: 0,
                    date = LocalDate.parse(doc.getString("date") ?: "")
                )
                emit(meal)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    suspend fun insertMeal(meal: Meal) {
        try {
            Log.d(TAG, "insertMeal: Starting to insert meal: ${meal.foodName}")
            Log.d(TAG, "insertMeal: Auth currentUser: ${auth.currentUser}")
            Log.d(TAG, "insertMeal: User ID: ${auth.currentUser?.uid}")
            Log.d(TAG, "insertMeal: User email: ${auth.currentUser?.email}")
            Log.d(TAG, "insertMeal: Is anonymous: ${auth.currentUser?.isAnonymous}")
            Log.d(TAG, "insertMeal: Firebase app name: ${firestore.app.name}")

            val collection = getMealsCollection()
            if (collection == null) {
                Log.e(TAG, "insertMeal: Collection is null - user not logged in!")
                Log.e(TAG, "insertMeal: Auth state - currentUser is: ${auth.currentUser}")
                return
            }

            val mealData = hashMapOf(
                "foodName" to meal.foodName,
                "mealType" to meal.mealType,
                "calories" to meal.calories,
                "protein" to meal.protein,
                "carbs" to meal.carbs,
                "fat" to meal.fat,
                "servingSize" to meal.servingSize,
                "date" to meal.date.toString()
            )

            Log.d(TAG, "insertMeal: Meal data prepared: $mealData")
            Log.d(TAG, "insertMeal: Saving to path: users/${auth.currentUser?.uid}/meals/${meal.id}")

            collection.document(meal.id).set(mealData).await()

            Log.d(TAG, "insertMeal: Meal saved successfully!")
        } catch (e: Exception) {
            Log.e(TAG, "insertMeal: ERROR saving meal", e)
            Log.e(TAG, "insertMeal: Error message: ${e.message}")
            Log.e(TAG, "insertMeal: Error cause: ${e.cause}")
        }
    }

    suspend fun updateMeal(meal: Meal) {
        try {
            val collection = getMealsCollection() ?: return
            val mealData = hashMapOf(
                "foodName" to meal.foodName,
                "mealType" to meal.mealType,
                "calories" to meal.calories,
                "protein" to meal.protein,
                "carbs" to meal.carbs,
                "fat" to meal.fat,
                "servingSize" to meal.servingSize,
                "date" to meal.date.toString()
            )
            collection.document(meal.id).set(mealData).await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun deleteMeal(meal: Meal) {
        try {
            val collection = getMealsCollection() ?: return
            collection.document(meal.id).delete().await()
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    suspend fun deleteAllMeals() {
        try {
            val collection = getMealsCollection() ?: return
            val snapshot = collection.get().await()
            snapshot.documents.forEach { doc ->
                doc.reference.delete()
            }
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    fun getTotalCaloriesForDate(date: LocalDate): Flow<Int> = callbackFlow {
        val collection = getMealsCollection()
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
                    it.getLong("calories")?.toInt() ?: 0
                } ?: 0

                trySend(total)
            }

        awaitClose { listener.remove() }
    }

    fun getTotalMacrosForDate(date: LocalDate): Flow<Triple<Float, Float, Float>> = callbackFlow {
        val collection = getMealsCollection()
        if (collection == null) {
            trySend(Triple(0f, 0f, 0f))
            close()
            return@callbackFlow
        }

        val dateStr = date.toString()
        val listener = collection
            .whereEqualTo("date", dateStr)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Triple(0f, 0f, 0f))
                    return@addSnapshotListener
                }

                val mealsForDate = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Triple(
                            doc.getDouble("protein")?.toFloat() ?: 0f,
                            doc.getDouble("carbs")?.toFloat() ?: 0f,
                            doc.getDouble("fat")?.toFloat() ?: 0f
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val result = Triple(
                    mealsForDate.sumOf { it.first.toDouble() }.toFloat(),
                    mealsForDate.sumOf { it.second.toDouble() }.toFloat(),
                    mealsForDate.sumOf { it.third.toDouble() }.toFloat()
                )

                trySend(result)
            }

        awaitClose { listener.remove() }
    }
}
