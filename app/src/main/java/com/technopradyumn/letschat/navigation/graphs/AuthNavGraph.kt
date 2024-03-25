package com.technopradyumn.letschat.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.technopradyumn.letschat.LetsChatViewModel
import com.technopradyumn.letschat.Screens.LoginScreen
import com.technopradyumn.letschat.Screens.SignupScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    viewModel: LetsChatViewModel

    ){

    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(
                navController = navController,
                viewModel = viewModel,
                onClick = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                },
                onSignUpClick = {
                    navController.navigate(AuthScreen.SignUp.route)
                },
            )
        }
        composable(route = AuthScreen.SignUp.route) {
            SignupScreen(navController = navController,
                viewModel = viewModel,
                onClick = {
                    navController.popBackStack(Graph.AUTHENTICATION,false)
                    navController.navigate(Graph.HOME)
                },
                onLogInClick= {
                    navController.navigate(AuthScreen.Login.route)
                }

            )
        }
    }

}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "LOGIN")
    object SignUp : AuthScreen(route = "SIGN_UP")
}