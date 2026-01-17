package com.polije.sipeperpolije

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.polije.sipeperpolije.core.navigation.NavigationRoot
import com.polije.sipeperpolije.di.appModule
import com.polije.sipeperpolije.theme.AppTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    val navController = rememberNavController()

    KoinApplication(application = { modules(appModule()) }){
        AppTheme {
            NavigationRoot(navController = navController)
        }
    }
}