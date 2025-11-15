package com.example.healthtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.ui.navigation.AppNavHost
import com.example.healthtracker.ui.theme.HealthtrackerTheme
import com.example.healthtracker.viewmodel.LoginViewModel
import com.example.healthtracker.viewmodel.MealViewModel
import com.example.healthtracker.viewmodel.ActivityViewModel
import com.example.healthtracker.viewmodel.GoalViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            HealthtrackerTheme {
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val mealViewModel: MealViewModel = viewModel()
                val activityViewModel: ActivityViewModel = viewModel()
                val goalViewModel: GoalViewModel = viewModel()

                AppNavHost(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    mealViewModel = mealViewModel,
                    activityViewModel = activityViewModel,
                    goalViewModel = goalViewModel
                )
            }
        }
    }
}
