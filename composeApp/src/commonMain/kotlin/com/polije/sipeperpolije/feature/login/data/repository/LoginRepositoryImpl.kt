package com.polije.sipeperpolije.feature.login.data.repository

import com.polije.sipeperpolije.core.log
import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class LoginRepositoryImpl(val supabase: SupabaseClient) : LoginRepository {

    override  suspend  fun login(username: String, password: String) {
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email = username
                this.password = password
            }
            log("Login success")
        }.onFailure {
            log("Login failed ${it.message}")

        }
    }
}