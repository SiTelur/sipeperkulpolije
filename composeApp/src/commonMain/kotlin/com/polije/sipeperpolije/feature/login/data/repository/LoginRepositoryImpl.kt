package com.polije.sipeperpolije.feature.login.data.repository

import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginRepositoryImpl(val supabase: SupabaseClient) : LoginRepository {

    override suspend fun login(username: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = username
            this.password = password
        }
    }


    override fun isLogin(): Flow<Boolean> = supabase.auth.sessionStatus.map {
        when (it) {
            is SessionStatus.Authenticated -> true
            is SessionStatus.NotAuthenticated -> false
            is SessionStatus.RefreshFailure -> false
            else -> false
        }
    }

}