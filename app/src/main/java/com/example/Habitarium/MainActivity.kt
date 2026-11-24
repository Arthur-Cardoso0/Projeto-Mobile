package com.example.Habitarium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.Habitarium.data.auth.SessionManager
import com.example.Habitarium.ui.screens.AuthScreen
import com.example.Habitarium.ui.screens.AuthState
import com.example.Habitarium.ui.screens.CatalogState
import com.example.Habitarium.ui.screens.HabitariumBottomBar
import com.example.Habitarium.ui.screens.HabitariumNavHost
import com.example.Habitarium.ui.theme.HabitariumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        setContent {
            HabitariumTheme {
                val auth: AuthState = viewModel()
                if (!auth.isLoggedIn) {
                    AuthScreen(auth)
                } else {
                    val navController = rememberNavController()
                    val state: CatalogState = viewModel()
                    Scaffold(bottomBar = { HabitariumBottomBar(navController) }) { padding ->
                        HabitariumNavHost(navController, state, auth, Modifier.padding(padding))
                    }
                }
            }
        }
    }
}