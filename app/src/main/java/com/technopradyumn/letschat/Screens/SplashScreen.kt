package com.technopradyumn.letschat.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.technopradyumn.letschat.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.technopradyumn.letschat.navigation.graphs.Graph

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_ic),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
        )
    }

    LaunchedEffect(true) {
        delay(1)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.popBackStack()
            navController.navigate(Graph.HOME)
        }else{
            navController.popBackStack()
            navController.navigate(Graph.AUTHENTICATION)
        }
    }
}
