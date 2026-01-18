package com.polije.sipeperpolije

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.polije.sipeperpolije.feature.login.presentation.screen.LoginScreen
import com.polije.sipeperpolije.feature.master.DosenScreen
import com.polije.sipeperpolije.feature.master.JadwalScreen
import com.polije.sipeperpolije.feature.master.MataKuliahDetailScreen
import com.polije.sipeperpolije.theme.AppTheme

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
    AppTheme() {
        LoginScreen(onLoginSuccess = {})
    }
}

@Preview
@Composable
fun DosenListPreview() {
    AppTheme() {
        DosenScreen()
    }
}

@Preview
@Composable
private fun JadwalListPreview() {
    AppTheme() {
        JadwalScreen()
    }
}

@Preview
@Composable
private fun MatkulDetailScreenPreview() {
    AppTheme(
    ) {
        MataKuliahDetailScreen()
    }
}