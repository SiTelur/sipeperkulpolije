package com.polije.sipeperpolije.feature.dashboard.domain.usecase

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository

class GenerateJadwalUseCase(private val dashboardRepository: DashboardRepository) {
    suspend operator fun invoke(semester: Semester): Result<Boolean> =
        dashboardRepository.generateJadwal(semester)

}