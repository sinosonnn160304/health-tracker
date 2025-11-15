package com.example.healthtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.model.HealthMetrics
import com.example.healthtracker.repository.UserRepository
import com.example.healthtracker.repository.MealRepository
import com.example.healthtracker.repository.ActivityRepository
import com.example.healthtracker.model.state.HealthMetricsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val mealRepository: MealRepository = MealRepository(),
    private val activityRepository: ActivityRepository = ActivityRepository()
) : ViewModel() {

    private val _healthMetricsState = MutableStateFlow(HealthMetricsState())
    val healthMetricsState: StateFlow<HealthMetricsState> = _healthMetricsState

    // Real-time total calories from meals for today
    private val _totalCaloriesToday = MutableStateFlow(0)
    val totalCaloriesToday: StateFlow<Int> = _totalCaloriesToday.asStateFlow()

    // Real-time macros from meals for today
    private val _totalProteinToday = MutableStateFlow(0f)
    val totalProteinToday: StateFlow<Float> = _totalProteinToday.asStateFlow()

    private val _totalCarbsToday = MutableStateFlow(0f)
    val totalCarbsToday: StateFlow<Float> = _totalCarbsToday.asStateFlow()

    private val _totalFatToday = MutableStateFlow(0f)
    val totalFatToday: StateFlow<Float> = _totalFatToday.asStateFlow()

    // Real-time activity minutes for today
    private val _totalActivityMinutesToday = MutableStateFlow(0)
    val totalActivityMinutesToday: StateFlow<Int> = _totalActivityMinutesToday.asStateFlow()

    // Real-time calories burned from activities for today
    private val _totalCaloriesBurnedToday = MutableStateFlow(0)
    val totalCaloriesBurnedToday: StateFlow<Int> = _totalCaloriesBurnedToday.asStateFlow()

    init {
        fetchHealthMetrics()
        observeRealTimeData()
    }

    private fun observeRealTimeData() {
        val today = LocalDate.now()

        // Observe real-time calories from meals
        viewModelScope.launch {
            mealRepository.getTotalCaloriesForDate(today).collect { calories ->
                _totalCaloriesToday.value = calories
            }
        }

        // Observe real-time macros from meals
        viewModelScope.launch {
            mealRepository.getTotalMacrosForDate(today).collect { (protein, carbs, fat) ->
                _totalProteinToday.value = protein
                _totalCarbsToday.value = carbs
                _totalFatToday.value = fat
            }
        }

        // Observe real-time activity minutes
        viewModelScope.launch {
            activityRepository.getTotalDurationForDate(today).collect { minutes ->
                _totalActivityMinutesToday.value = minutes
            }
        }

        // Observe real-time calories burned from activities
        viewModelScope.launch {
            activityRepository.getTotalCaloriesBurnedForDate(today).collect { calories ->
                _totalCaloriesBurnedToday.value = calories
            }
        }
    }

    fun fetchHealthMetrics() {
        viewModelScope.launch {
            _healthMetricsState.value = HealthMetricsState(isLoading = true)
            try {
                val metrics = userRepository.getHealthMetrics() // Lấy dữ liệu từ Firestore
                _healthMetricsState.value = HealthMetricsState(metrics = metrics)
            } catch (e: Exception) {
                _healthMetricsState.value = HealthMetricsState(error = e.localizedMessage)
            }
        }
    }
}
