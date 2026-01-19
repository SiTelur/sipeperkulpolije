package com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel

sealed class DashboardEvent {
    object LogoutSuccess : DashboardEvent()
    data class LogoutFailed(val message: String) : DashboardEvent()

}