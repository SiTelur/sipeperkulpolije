package com.polije.sipeperpolije.feature.dashboard.data.repository

import com.polije.sipeperpolije.feature.dashboard.data.model.ActivityItemModel
import com.polije.sipeperpolije.feature.dashboard.data.model.DashboardModel
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order

class DashboardRepositoryImpl(private val supabase: SupabaseClient) : DashboardRepository {
    override suspend fun logout() {
        supabase.auth.signOut()
    }

    override suspend fun fetchDashboard(): Result<DashboardModel> = runCatching {
        val dosenCount = supabase.from("dosen")
            .select {
                count(Count.EXACT)
                head = true
            }
            .countOrNull()?.toInt()

        val matkulCount = supabase
            .from("mata_kuliah")
            .select {
                count(Count.EXACT)
                head
            }.countOrNull()?.toInt()
        val recentActivities = supabase
            .from("audit_log")
            .select {
                order("changed_at", order = Order.DESCENDING)
                limit(3)
            }
            .decodeList<ActivityItemModel>()

        return Result.success(
            DashboardModel(
                dosenCount ?: 0, matkulCount ?: 0,
                isLastGeneratedScheduleSuccess = false,
                recentActivity = recentActivities
            )
        )
    }.onFailure {
        return Result.failure(it)
    }
}