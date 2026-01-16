package com.polije.sipeperpolije


actual object AppConfig {
    actual val apiBaseUrl : String = BuildConfig.API_BASE_URL
    actual val apiKey: String = BuildConfig.API_KEY
}