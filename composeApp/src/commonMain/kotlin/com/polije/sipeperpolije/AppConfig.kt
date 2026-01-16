package com.polije.sipeperpolije

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect  object AppConfig {
    val apiBaseUrl: String
    val apiKey: String
}