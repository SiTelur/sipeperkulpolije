package com.polije.sipeperpolije.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.polije.sipeperpolije.feature.dashboard.DashboardScreen
import com.polije.sipeperpolije.feature.login.LoginScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoot(navController : NavHostController,modifier: Modifier = Modifier){
    NavHost(navController = navController, startDestination = NavigationRoute.Login, modifier = modifier){
        composable<NavigationRoute.Dashboard>{
            DashboardScreen()
        }
        composable<NavigationRoute.Login>{
            LoginScreen({navController.navigate(NavigationRoute.Dashboard){
                popUpTo(NavigationRoute.Login){
                    inclusive = true
                }
            } })
        }

    }
}

@Serializable
sealed class NavigationRoute {

    @Serializable
    object Dashboard : NavigationRoute()
    @Serializable
    object Login : NavigationRoute()
}
