package com.polije.sipeperpolije.feature.dashboard.domain.usecase

import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository

class LogoutUseCase(private val dashboardRepository: DashboardRepository) {
    suspend operator fun invoke() {
        dashboardRepository.logout()
    }
}