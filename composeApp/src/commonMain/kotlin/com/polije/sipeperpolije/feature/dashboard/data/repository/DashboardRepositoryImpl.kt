package com.polije.sipeperpolije.feature.dashboard.data.repository

import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class DashboardRepositoryImpl(private val supabase : SupabaseClient) : DashboardRepository {
    override suspend fun logout() {
        supabase.auth.signOut()
    }
}