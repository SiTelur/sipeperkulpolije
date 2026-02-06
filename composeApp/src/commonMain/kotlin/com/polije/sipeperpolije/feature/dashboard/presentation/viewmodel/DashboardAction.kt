package com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel

import com.polije.sipeperpolije.core.Semester

sealed class DashboardAction {
    object OnLogoutPressed : DashboardAction()
    data class OnGenerateJadwal(val semester: Semester) : DashboardAction()

}


