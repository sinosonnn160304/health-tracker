package com.example.healthtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.model.HealthGoal
import com.example.healthtracker.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(
    private val repository: GoalRepository = GoalRepository()
) : ViewModel() {

    private val _currentGoal = MutableStateFlow<HealthGoal?>(null)
    val currentGoal: StateFlow<HealthGoal?> = _currentGoal.asStateFlow()

    private val _allGoals = MutableStateFlow<List<HealthGoal>>(emptyList())
    val allGoals: StateFlow<List<HealthGoal>> = _allGoals.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultGoalExists()
        }
        loadCurrentGoal()
        loadAllGoals()
    }

    private fun loadCurrentGoal() {
        viewModelScope.launch {
            repository.getCurrentGoal().collect { goal ->
                _currentGoal.value = goal
            }
        }
    }

    private fun loadAllGoals() {
        viewModelScope.launch {
            repository.getAllGoals().collect { goals ->
                _allGoals.value = goals
            }
        }
    }

    fun setGoal(goal: HealthGoal) {
        viewModelScope.launch {
            repository.insertGoal(goal)
            loadCurrentGoal()
            loadAllGoals()
        }
    }

    fun updateGoal(goal: HealthGoal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
            loadCurrentGoal()
            loadAllGoals()
        }
    }

    fun deleteGoal(goal: HealthGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            loadCurrentGoal()
            loadAllGoals()
        }
    }

    fun isCalorieGoalMet(currentCalories: Int): Boolean {
        val goal = _currentGoal.value?.dailyCalorieGoal ?: return false
        return currentCalories in (goal - 100)..(goal + 100)
    }

    fun isProteinGoalMet(currentProtein: Float): Boolean {
        val goal = _currentGoal.value?.proteinGoal ?: return false
        return currentProtein >= goal
    }

    fun isCarbsGoalMet(currentCarbs: Float): Boolean {
        val goal = _currentGoal.value?.carbsGoal ?: return false
        return currentCarbs >= goal
    }

    fun isFatGoalMet(currentFat: Float): Boolean {
        val goal = _currentGoal.value?.fatGoal ?: return false
        return currentFat >= goal
    }

    fun calculateCalorieProgress(currentCalories: Int): Float {
        val goal = _currentGoal.value?.dailyCalorieGoal ?: return 0f
        return (currentCalories.toFloat() / goal.toFloat()).coerceIn(0f, 1.5f)
    }

    fun calculateProteinProgress(currentProtein: Float): Float {
        val goal = _currentGoal.value?.proteinGoal ?: return 0f
        return (currentProtein / goal).coerceIn(0f, 1.5f)
    }

    fun calculateCarbsProgress(currentCarbs: Float): Float {
        val goal = _currentGoal.value?.carbsGoal ?: return 0f
        return (currentCarbs / goal).coerceIn(0f, 1.5f)
    }

    fun calculateFatProgress(currentFat: Float): Float {
        val goal = _currentGoal.value?.fatGoal ?: return 0f
        return (currentFat / goal).coerceIn(0f, 1.5f)
    }
}
