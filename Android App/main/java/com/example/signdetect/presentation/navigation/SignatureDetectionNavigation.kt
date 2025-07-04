package com.example.signdetect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.signdetect.presentation.screens.home.HomeScreen
import com.example.signdetect.presentation.screens.comparison.ComparisonScreen
import com.example.signdetect.presentation.screens.result.ResultScreen

@Composable
fun SignatureDetectionNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToComparison = {
                    navController.navigate(Screen.Comparison.route)
                }
            )
        }
        
        composable(Screen.Comparison.route) {
            ComparisonScreen(
                onNavigateToResult = { resultId ->
                    navController.navigate(Screen.Result.createRoute(resultId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Result.route + "/{resultId}",
            arguments = Screen.Result.arguments
        ) {
            ResultScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Comparison : Screen("comparison")
    object Result : Screen("result") {
        val arguments = listOf(
            navArgument("resultId") { type = NavType.StringType }
        )
        
        fun createRoute(resultId: String) = "$route/$resultId"
    }
} 