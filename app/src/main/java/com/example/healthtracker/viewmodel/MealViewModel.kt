package com.example.healthtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.model.Meal
import com.example.healthtracker.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealViewModel(
    private val repository: MealRepository = MealRepository()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _mealsForDate = MutableStateFlow<List<Meal>>(emptyList())
    val mealsForDate: StateFlow<List<Meal>> = _mealsForDate.asStateFlow()

    private val _allMeals = MutableStateFlow<List<Meal>>(emptyList())
    val allMeals: StateFlow<List<Meal>> = _allMeals.asStateFlow()

    init {
        loadMealsForDate(_selectedDate.value)
        loadAllMeals()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadMealsForDate(date)
    }

    private fun loadMealsForDate(date: LocalDate) {
        viewModelScope.launch {
            repository.getMealsByDate(date).collect { meals ->
                _mealsForDate.value = meals
            }
        }
    }

    private fun loadAllMeals() {
        viewModelScope.launch {
            repository.getAllMeals().collect { meals ->
                _allMeals.value = meals
            }
        }
    }

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            repository.insertMeal(meal)
            loadMealsForDate(_selectedDate.value)
            loadAllMeals()
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            repository.updateMeal(meal)
            loadMealsForDate(_selectedDate.value)
            loadAllMeals()
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
            loadMealsForDate(_selectedDate.value)
            loadAllMeals()
        }
    }

    fun getMealsForDateRange(startDate: LocalDate, endDate: LocalDate): StateFlow<List<Meal>> {
        val flow = MutableStateFlow<List<Meal>>(emptyList())
        viewModelScope.launch {
            repository.getMealsForDateRange(startDate, endDate).collect { meals ->
                flow.value = meals
            }
        }
        return flow.asStateFlow()
    }

    fun getTotalCaloriesForDate(date: LocalDate): Int {
        return _mealsForDate.value
            .filter { it.date == date }
            .sumOf { it.calories }
    }

    fun getTotalProteinForDate(date: LocalDate): Float {
        return _mealsForDate.value
            .filter { it.date == date }
            .sumOf { it.protein.toDouble() }
            .toFloat()
    }

    fun getTotalCarbsForDate(date: LocalDate): Float {
        return _mealsForDate.value
            .filter { it.date == date }
            .sumOf { it.carbs.toDouble() }
            .toFloat()
    }

    fun getTotalFatForDate(date: LocalDate): Float {
        return _mealsForDate.value
            .filter { it.date == date }
            .sumOf { it.fat.toDouble() }
            .toFloat()
    }
}