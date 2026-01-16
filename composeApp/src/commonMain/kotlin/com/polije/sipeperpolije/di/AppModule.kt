package com.polije.sipeperpolije.di

import com.polije.sipeperpolije.AppConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single { createSupabaseClient(
        supabaseUrl = AppConfig.apiBaseUrl,
        supabaseKey = AppConfig.apiKey
    ) {
        install(Postgrest)
    } }
}