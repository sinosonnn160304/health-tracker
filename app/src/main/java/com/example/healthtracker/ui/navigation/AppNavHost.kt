package com.example.healthtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthtracker.ui.screen.*
import com.example.healthtracker.viewmodel.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    mealViewModel: MealViewModel,
    activityViewModel: ActivityViewModel,
    goalViewModel: GoalViewModel,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val userViewModel: UserViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Routes.Launch
    ) {
        // ================== LAUNCH SCREEN ==================
        composable(Routes.Launch) {
            LaunchScreen(
                onNavigateToSplash = {
                    navController.navigate(Routes.Splash) {
                        popUpTo(Routes.Launch) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Launch) { inclusive = true }
                    }
                }
            )
        }

        // ================== SPLASH SCREEN ==================
        composable(Routes.Splash) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Routes.Login) },
                onNavigateToSignup = { navController.navigate(Routes.Signup) }
            )
        }

        // ================== LOGIN SCREEN ==================
        composable(Routes.Login) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Intro1) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate(Routes.Signup) },
                onBackToSplash = { navController.navigate(Routes.Splash) }
            )
        }

        // ================== SIGNUP SCREEN ==================
        composable(Routes.Signup) {
            SignupScreen(
                onBackToSplash = { navController.navigate(Routes.Splash) },
                onNavigateToLogin = { navController.navigate(Routes.Login) }
            )
        }

        // ================== INTRO SCREENS ==================
        composable(Routes.Intro1) {
            IntroScreen(
                step = 1,
                onNext = { navController.navigate(Routes.Intro2) },
                onSkip = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Intro1) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Intro2) {
            IntroScreen(
                step = 2,
                onNext = { navController.navigate(Routes.Intro3) },
                onBack = { navController.popBackStack() },
                onSkip = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Intro1) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Intro3) {
            IntroScreen(
                step = 3,
                onNext = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Intro1) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onSkip = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Intro1) { inclusive = true }
                    }
                }
            )
        }

        // ================== DASHBOARD SCREEN ==================
        composable(Routes.Dashboard) {
            DashboardScreen(
                mealViewModel = mealViewModel,
                activityViewModel = activityViewModel,
                goalViewModel = goalViewModel,
                onNavigateToMeals = { navController.navigate(Routes.MealTracker) },
                onNavigateToActivity = { navController.navigate(Routes.ActivityTracker) },
                onNavigateToCharts = { navController.navigate(Routes.Charts) },
                onNavigateToHealthGoals = { navController.navigate(Routes.HealthGoals) },
                onNavigateToHealthMetrics = { navController.navigate(Routes.HealthMetrics) }, // <-- thêm đây
                loginViewModel = loginViewModel,
                onNavigateToUserProfile = { navController.navigate(Routes.UserProfile) },
                onLogout = {
                    navController.navigate(Routes.Splash) {
                        popUpTo(Routes.Dashboard) { inclusive = true }
                    }
                }
            )
        }

        // ================== MEAL TRACKER SCREEN ==================
        composable(Routes.MealTracker) {
            MealTrackerScreen(
                mealViewModel = mealViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ================== ACTIVITY TRACKER SCREEN ==================
        composable(Routes.ActivityTracker) {
            ActivityTrackerScreen(
                activityViewModel = activityViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ================== CHARTS SCREEN ==================
        composable(Routes.Charts) {
            ChartsScreen(
                mealViewModel = mealViewModel,
                activityViewModel = activityViewModel,
                goalViewModel = goalViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ================== USER PROFILE SCREEN ==================
        composable(Routes.UserProfile) {
            UserProfileScreen(
                loginViewModel = loginViewModel,
                onLogout = {
                    navController.navigate(Routes.Splash) {
                        popUpTo(Routes.UserProfile) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange
            )
        }

        // ================== HEALTH GOALS SCREEN ==================
        composable(Routes.HealthGoals) {
            HealthGoalsScreen(
                goalViewModel = goalViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ================== HEALTH METRICS SCREEN ==================
        composable(Routes.HealthMetrics) {
            HealthMetricsScreen(
                userViewModel = userViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}
