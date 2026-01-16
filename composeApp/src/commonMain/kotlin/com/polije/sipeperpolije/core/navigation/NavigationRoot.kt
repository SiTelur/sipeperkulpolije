package com.polije.sipeperpolije.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.polije.sipeperpolije.feature.dashboard.DashboardScreen
import com.polije.sipeperpolije.feature.login.presentation.screen.LoginScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoot(navController : NavHostController,modifier: Modifier = Modifier){
    NavHost(navController = navController, startDestination = Login, modifier = modifier){
        composable<Dashboard>{
            DashboardScreen()
        }
        composable<Login>{
            LoginScreen({navController.navigate(Dashboard){
                popUpTo(Login){
                    inclusive = true
                }
            } })
        }

    }
}



    @Serializable
    object Dashboard
    @Serializable
    object Login

