package com.example.healthtracker.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.model.ActivityRecord
import com.example.healthtracker.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActivityViewModel(
    private val repository: ActivityRepository = ActivityRepository()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _activitiesForDate = MutableStateFlow<List<ActivityRecord>>(emptyList())
    val activitiesForDate: StateFlow<List<ActivityRecord>> = _activitiesForDate.asStateFlow()

    private val _allActivities = MutableStateFlow<List<ActivityRecord>>(emptyList())
    val allActivities: StateFlow<List<ActivityRecord>> = _allActivities.asStateFlow()

    init {
        loadActivitiesForDate(_selectedDate.value)
        loadAllActivities()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadActivitiesForDate(date)
    }

    private fun loadActivitiesForDate(date: LocalDate) {
        viewModelScope.launch {
            repository.getActivitiesByDate(date).collect { activities ->
                _activitiesForDate.value = activities
            }
        }
    }

    private fun loadAllActivities() {
        viewModelScope.launch {
            repository.getAllActivities().collect { activities ->
                _allActivities.value = activities
            }
        }
    }

    fun addActivity(activity: ActivityRecord) {
        viewModelScope.launch {
            repository.insertActivity(activity)
            loadActivitiesForDate(_selectedDate.value)
            loadAllActivities()
        }
    }

    fun updateActivity(activity: ActivityRecord) {
        viewModelScope.launch {
            repository.updateActivity(activity)
            loadActivitiesForDate(_selectedDate.value)
            loadAllActivities()
        }
    }

    fun deleteActivity(activity: ActivityRecord) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            loadActivitiesForDate(_selectedDate.value)
            loadAllActivities()
        }
    }

    fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): StateFlow<List<ActivityRecord>> {
        val flow = MutableStateFlow<List<ActivityRecord>>(emptyList())
        viewModelScope.launch {
            repository.getActivitiesForDateRange(startDate, endDate).collect { activities ->
                flow.value = activities
            }
        }
        return flow.asStateFlow()
    }

    fun getTotalCaloriesBurnedForDate(date: LocalDate): Int {
        return _activitiesForDate.value
            .filter { it.date == date }
            .sumOf { it.caloriesBurned }
    }

    fun getTotalDurationForDate(date: LocalDate): Int {
        return _activitiesForDate.value
            .filter { it.date == date }
            .sumOf { it.durationMinutes }
    }
}
