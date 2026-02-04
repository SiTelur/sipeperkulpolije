package com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel

sealed class DashboardEvent {
    object LogoutSuccess : DashboardEvent()
    data class LogoutFailed(val message: String) : DashboardEvent()
    data class FetchDashboardFailed(val message: String) : DashboardEvent()
    object GenerateJadwalSuccess : DashboardEvent()
    data class GenerateJadwalFailed(val message: String) : DashboardEvent()


}