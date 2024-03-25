package com.technopradyumn.letschat.navigation.graphs

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.technopradyumn.letschat.LetsChatViewModel
import com.technopradyumn.letschat.Screens.HomeScreen
import com.technopradyumn.letschat.Screens.SplashScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    val viewModel:LetsChatViewModel = hiltViewModel()
    viewModel.BackHandler1(onBackPressed = { navController.navigateUp() })
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.SPLASH
    ) {
        authNavGraph(navController = navController,viewModel)
        composable(route = Graph.HOME) {
            HomeScreen()
        }
        composable(route = Graph.SPLASH) {
            SplashScreen(navController = navController)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val SPLASH = "splash_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val CHAT = "chat_graph"
    const val STATUS = "status_graph"
    const val PROFILE = "profile_graph"
}