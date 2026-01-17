package com.polije.sipeperpolije.di

import com.polije.sipeperpolije.BuildKonfig
import com.polije.sipeperpolije.feature.login.data.repository.LoginRepositoryImpl
import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase
import com.polije.sipeperpolije.feature.login.presentation.viewmodel.LoginViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun  appModule() = module {
    single { createSupabaseClient(
        supabaseUrl = BuildKonfig.API_BASE_URL,
        supabaseKey = BuildKonfig.API_KEY
    ) {
        install(Postgrest)
    } }

    singleOf(::LoginRepositoryImpl) { bind <LoginRepository>() }
    singleOf(::LoginUseCase)

    viewModelOf(::LoginViewModel)
}