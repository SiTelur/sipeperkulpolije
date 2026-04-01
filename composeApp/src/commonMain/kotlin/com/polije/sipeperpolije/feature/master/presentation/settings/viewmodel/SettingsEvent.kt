package com.polije.sipeperpolije.feature.master.presentation.settings.viewmodel

sealed class SettingsEvent {
    object LogoutSuccess : SettingsEvent()
    data class LogoutFailed(val message: String) : SettingsEvent()
}