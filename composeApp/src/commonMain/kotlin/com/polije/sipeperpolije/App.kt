package com.polije.sipeperpolije

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.belajarkmp.ui.theme.MainTheme
import com.polije.sipeperpolije.core.navigation.NavigationRoot
import com.polije.sipeperpolije.di.appModule
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.module

import sistempenjadwalanperkuliahanpolitekniknegerijember.composeapp.generated.resources.Res
import sistempenjadwalanperkuliahanpolitekniknegerijember.composeapp.generated.resources.compose_multiplatform

@Composable
fun App() {
    val navController = rememberNavController()

    KoinApplication(application = { modules(appModule()) }){
        MainTheme {
            NavigationRoot(navController = navController)
        }
    }
}