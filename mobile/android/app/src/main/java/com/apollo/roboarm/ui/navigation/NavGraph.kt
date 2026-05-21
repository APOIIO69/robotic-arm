package com.apollo.roboarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.apollo.roboarm.ui.screens.details.RobotDetailsScreen
import com.apollo.roboarm.ui.screens.home.HomeScreen
import com.apollo.roboarm.ui.screens.line.LineDetailScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class LineDetail(val id: Int, val name: String)

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
                onNavigateToLine = { id, name ->
                    navController.navigate(LineDetail(id, name))
                }
            )
        }
        composable<LineDetail> { backStackEntry ->
            val detail: LineDetail = backStackEntry.toRoute()
            LineDetailScreen(
                lineId = detail.id,
                lineName = detail.name,
                onBack = { navController.popBackStack() },
                onNavigateToRobot = { robotId ->
                    navController.navigate(RobotDetail(robotId))
                }
            )
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
