package com.polije.sipeperpolije.feature.dashboard.domain.repository

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntity

interface DashboardRepository {
    suspend fun logout()
    suspend fun fetchDashboard(): Result<DashboardEntity>
    suspend fun generateJadwal(semester: Semester, workshopTime: Int? = null): Result<Boolean>
    suspend fun previewJadwal(semester: Semester): List<PreviewJadwalEntity>
}