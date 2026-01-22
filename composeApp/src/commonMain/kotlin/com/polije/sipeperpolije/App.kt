package com.polije.sipeperpolije

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.polije.sipeperpolije.core.navigation.NavigationRoot
import com.polije.sipeperpolije.di.appModule
import com.polije.sipeperpolije.theme.AppTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    val navController = rememberNavController()

    KoinApplication(application = { modules(appModule()) }) {
        val snackbarHostState = remember { SnackbarHostState() }
        AppTheme {
            CompositionLocalProvider(value = LocalSnackbarHostState provides snackbarHostState) {

                NavigationRoot(navController = navController)
            }
        }
    }
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State")
}