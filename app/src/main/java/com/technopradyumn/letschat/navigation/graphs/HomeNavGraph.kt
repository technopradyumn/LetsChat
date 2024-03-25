package com.technopradyumn.letschat.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.technopradyumn.letschat.LetsChatViewModel
import com.technopradyumn.letschat.Screens.ChatListScreen
import com.technopradyumn.letschat.Screens.ProfileScreen
import com.technopradyumn.letschat.Screens.SingleChatScreen
import com.technopradyumn.letschat.Screens.SingleStatusScreen
import com.technopradyumn.letschat.Screens.StatusScreen
import com.technopradyumn.letschat.navigation.BottomBarScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    val viewModel:LetsChatViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.ChatList.route
    ) {
        composable(route = BottomBarScreen.ChatList.route) {
            ChatListScreen(
                navController = navController,
                viewModel = viewModel,
                onClick = { userId ->
                    navController.navigate(ChatSc.SingleChat.route + "/$userId")
                }
            )
        }

        composable(route = BottomBarScreen.Status.route) {
            StatusScreen(
                navController = navController,
                viewModel = viewModel,
                onClick = { userId ->
                    navController.navigate(StatusSc.SingleStatus.route + "/$userId")
                }
            )
        }

        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen(
                navController = navController,
                viewModel = viewModel,
                onLogOut = {
                    navController.popBackStack()
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.HOME) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        chatListNavGraph(navController = navController)
        statusNavGraph(navController = navController)
        profileNavGraph(navController = navController)

        authNavGraph(navController = navController,viewModel)

    }
}


fun NavGraphBuilder.chatListNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.CHAT,
        startDestination = ChatSc.SingleChat.route
    ) {
        composable(route = ChatSc.SingleChat.route + "/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            SingleChatScreen(
                chatId = chatId,
                function = {
                    navController.popBackStack()
                }
            )
        }

    }
}

fun NavGraphBuilder.statusNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.STATUS,
        startDestination = StatusSc.SingleStatus.route
    ) {
        composable(route = StatusSc.SingleStatus.route + "/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SingleStatusScreen(userId = userId)
        }
    }
}

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.PROFILE,
        startDestination = ProfileSc.Setting.route
    ) {
        composable(route = ProfileSc.Setting.route) {
//            SettingScreen(
//                name = ProfileSc.Setting.route,
//                function = {
//
//                }
//            )
        }

    }
}


sealed class ChatSc(val route: String) {
    object SingleChat : ChatSc(route = "SINGLECHAT")
    object Overview : ChatSc(route = "OVERVIEW")
}

sealed class StatusSc(val route: String) {
    object SingleStatus : StatusSc(route = "SINGLESTATUS")
}

sealed class ProfileSc(val route: String) {
    object Setting : ProfileSc(route = "Setting")
}