package com.polije.sipeperpolije

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.belajarkmp.ui.theme.MainTheme
import com.polije.sipeperpolije.feature.dashboard.list.DosenScreen
import com.polije.sipeperpolije.feature.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    MainTheme(){
        LoginScreen(onLoginSuccess = {})
    }}

@Preview
@Composable
fun DosenListPreview(){
    MainTheme() {
        DosenScreen()
    }
}
