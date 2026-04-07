package com.polije.sipeperpolije.feature.dashboard.domain.usecase

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntity
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository

class PreviewJadwalUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(semester: Semester): Result<List<PreviewJadwalEntity>> =
        repository.previewJadwal(semester)
}
