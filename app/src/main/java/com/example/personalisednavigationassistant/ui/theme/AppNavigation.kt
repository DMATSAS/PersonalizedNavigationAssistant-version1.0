package com.example.personalisednavigationassistant.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personalisednavigationassistant.viewmodel.TourViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Detail : Screen("detail/{monumentId}") {

        fun createRoute(monumentId: String) = "detail/$monumentId"
    }
}

@Composable
fun AppNavigation(viewModel: TourViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onExploreClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Home.route) {
            HomeScreen(
                onScanClick = {

                    navController.navigate(Screen.Camera.route)
                },
                onMonumentClick = { clickedMonumentId ->
                    viewModel.scanMonument(poiId = clickedMonumentId)
                    navController.navigate(route = Screen.Detail.createRoute(clickedMonumentId))
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToHelp = {
                    navController.navigate("help_center")
                },
                onLogout = {

                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Η Οθόνη της Κάμερας
        composable(Screen.Camera.route) {
            CameraScreen(
                onQrCodeScanned = { scannedId ->
                    viewModel.scanMonument(scannedId)
                    navController.navigate(Screen.Detail.createRoute(scannedId)) {
                        popUpTo(Screen.Camera.route) { inclusive = true }
                    }
                }
            )
        }

        // Η Οθόνη Λεπτομερειών
        composable(Screen.Detail.route) { backStackEntry ->
            val monumentId = backStackEntry.arguments?.getString("monumentId") ?: "Unknown"

            MonumentDetailScreen(
                viewModel = viewModel,
                monumentId = monumentId,
                monumentName = monumentId.replace("_", " ").uppercase(),
                onBackClick = {
                    viewModel.resetState()
                    navController.popBackStack()
                }
            )
        }

        // Οθόνη Ρυθμίσεων
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Οθόνη Βοήθειας
        composable("help_center") {
            HelpCenterScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}