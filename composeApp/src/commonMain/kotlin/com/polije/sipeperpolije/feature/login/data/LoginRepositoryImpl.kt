package com.polije.sipeperpolije.feature.login.data

import com.polije.sipeperpolije.feature.login.domain.LoginRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class LoginRepositoryImpl(val supabase: SupabaseClient) : LoginRepository {

    override  suspend  fun login(username: String, password: String) {
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email = "example@email.com"
                this.password = "password"
            }
        }
    }
}