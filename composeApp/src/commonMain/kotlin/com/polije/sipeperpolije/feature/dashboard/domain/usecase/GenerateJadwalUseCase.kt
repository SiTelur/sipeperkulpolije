package com.polije.sipeperpolije.feature.dashboard.domain.usecase

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity

class GenerateJadwalUseCase(private val dashboardRepository: DashboardRepository) {
    suspend operator fun invoke(
        title: String,
        semester: Semester,
        overrideJamPraktek: Int?
    ): Result<JadwalEntity> =
        dashboardRepository.generateJadwal(title, semester, overrideJamPraktek)

}