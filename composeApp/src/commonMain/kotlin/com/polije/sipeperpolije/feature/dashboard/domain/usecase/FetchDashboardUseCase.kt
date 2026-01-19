package com.polije.sipeperpolije.feature.dashboard.domain.usecase

import com.polije.sipeperpolije.feature.dashboard.data.model.toEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository

class FetchDashboardUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): Result<DashboardEntity> {
        return repository.fetchDashboard().map { dashboardModel ->
            dashboardModel.toEntity()
        }
    }
}