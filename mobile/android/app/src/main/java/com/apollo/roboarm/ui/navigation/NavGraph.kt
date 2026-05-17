package com.apollo.roboarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.apollo.roboarm.ui.screens.details.RobotDetailsScreen
import com.apollo.roboarm.ui.screens.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class LineDetail(val id: Int)

@Serializable
data class RobotDetail(val id: Int)

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                onNavigateToLine = { id ->
                    navController.navigate(LineDetail(id))
                }
            )
        }
        composable<LineDetail> {
            // TODO: Implement LineDetailScreen
        }
        composable<RobotDetail> { backStackEntry ->
            val detail: RobotDetail = backStackEntry.toRoute()
            RobotDetailsScreen(
                robotId = detail.id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
