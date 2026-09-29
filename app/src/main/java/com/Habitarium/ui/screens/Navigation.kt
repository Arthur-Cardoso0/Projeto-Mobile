package com.example.Habitarium.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home     : Screen("home",     "Início",    Icons.Outlined.Home)
    object Search   : Screen("search",   "Busca",     Icons.Outlined.Search)
    object Compare  : Screen("compare",  "Comparar",  Icons.Outlined.CompareArrows)
    object Budget   : Screen("budget",   "Orçamento", Icons.Outlined.Description)
    object Account  : Screen("account",  "Conta",     Icons.Outlined.Person)
}

val bottomNavItems = listOf(
    Screen.Home, Screen.Search, Screen.Compare, Screen.Budget, Screen.Account
)

@Composable
fun HabitariumNavHost(navController: NavHostController, state: CatalogState, modifier: Modifier = Modifier) {
    NavHost(navController = navController, modifier = modifier, startDestination = Screen.Home.route) {
        composable(Screen.Home.route)    { HomeScreen(navController, state) }
        composable(Screen.Search.route)  { SearchScreen(navController, state) }
        composable(Screen.Compare.route) { CompareScreen(navController, state) }
        composable(Screen.Budget.route)  { BudgetScreen(navController, state) }
        // Conta local enquanto a autenticação não estiver integrada.
        composable(Screen.Account.route) { Column(Modifier.fillMaxSize().padding(24.dp)) { Text("Minha conta", style = MaterialTheme.typography.headlineMedium); Spacer(Modifier.height(16.dp)); Text("Você está usando o modo de demonstração. Seu orçamento é salvo neste aparelho. O acesso com conta ainda não está disponível.") } }
    }
}

@Composable
fun HabitariumBottomBar(navController: NavHostController) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick  = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        // Início deve abrir o catálogo, sem restaurar destinos acima dele.
                        restoreState = screen.route != Screen.Home.route
                    }
                },
                icon  = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) }
            )
        }
    }
}
