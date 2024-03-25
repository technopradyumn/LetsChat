package com.technopradyumn.letschat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WebStories
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WebStories
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    object ChatList : BottomBarScreen(
        route = "CHAT",
        title = "CHAT",
        unselectedIcon = Icons.Outlined.Chat,
        selectedIcon = Icons.Filled.Chat
    )

    object Status : BottomBarScreen(
        route = "STATUS",
        title = "STATUS",
        unselectedIcon = Icons.Outlined.WebStories,
        selectedIcon = Icons.Filled.WebStories
    )

    object Profile : BottomBarScreen(
        route = "PROFILE",
        title = "PROFILE",
        unselectedIcon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}
