package com.polije.sipeperpolije.feature.dashboard.domain.repository

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity

interface DashboardRepository {
    suspend fun logout()
    suspend fun fetchDashboard(): Result<DashboardEntity>
    suspend fun generateJadwal(
        title: String,
        semester: Semester,
        workshopTime: Int? = null
    ): Result<JadwalEntity>

    suspend fun previewJadwal(semester: Semester): Result<List<PreviewJadwalEntity>>
}