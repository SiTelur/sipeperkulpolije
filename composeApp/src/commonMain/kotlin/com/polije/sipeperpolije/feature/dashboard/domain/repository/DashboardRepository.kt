package com.polije.sipeperpolije.feature.dashboard.domain.repository

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.data.model.DashboardModel

interface DashboardRepository {
    suspend fun logout()
    suspend fun fetchDashboard(): Result<DashboardModel>
    suspend fun generateJadwal(semester: Semester, workshopTime: Int? = null): Result<Boolean>
}