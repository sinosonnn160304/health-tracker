package com.example.healthtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.ui.navigation.AppNavHost
import com.example.healthtracker.ui.screen.NoInternetScreen
import com.example.healthtracker.ui.theme.HealthtrackerTheme
import com.example.healthtracker.viewmodel.*
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            HealthtrackerTheme(darkTheme = isDarkMode) {
            val navController = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val mealViewModel: MealViewModel = viewModel()
                val activityViewModel: ActivityViewModel = viewModel()
                val goalViewModel: GoalViewModel = viewModel()

                // Network state
                val networkMonitor = remember { NetworkMonitor(this) }
                val isConnected by networkMonitor.isConnected.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        mealViewModel = mealViewModel,
                        activityViewModel = activityViewModel,
                        goalViewModel = goalViewModel,
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it }
                    )

                    if (!isConnected) {
                        // Overlay No Internet
                        NoInternetScreen(
                            onRetry = { }
                        )
                    }
                }
            }
        }
    }
}

// ====================== Network Monitor ======================
class NetworkMonitor(context: Context) {
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true
            }

            override fun onLost(network: Network) {
                _isConnected.value = false
            }
        }

        val request = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Check initial state
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        _isConnected.value =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
