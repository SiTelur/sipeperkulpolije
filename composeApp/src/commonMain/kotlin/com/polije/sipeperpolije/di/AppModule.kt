package com.polije.sipeperpolije.di

import com.polije.sipeperpolije.BuildKonfig
import com.polije.sipeperpolije.feature.dashboard.data.repository.DashboardRepositoryImpl
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.FetchDashboardUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.LogoutUseCase
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardViewModel
import com.polije.sipeperpolije.feature.login.data.repository.LoginRepositoryImpl
import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository
import com.polije.sipeperpolije.feature.login.domain.usecase.IsLoginUseCase
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase
import com.polije.sipeperpolije.feature.login.presentation.viewmodel.LoginViewModel
import com.polije.sipeperpolije.feature.master.data.repository.MasterRepositoryImpl
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import com.polije.sipeperpolije.feature.master.domain.usecase.ListDosenPagingUseCase
import com.polije.sipeperpolije.feature.master.presentation.viewmodel.dosen.ListDosenViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {

    single {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.API_BASE_URL,
            supabaseKey = BuildKonfig.API_KEY
        ) {
            defaultLogLevel = LogLevel.DEBUG
            install(Postgrest)
            install(Auth) {
                flowType = FlowType.PKCE
            }
            defaultSerializer = KotlinXSerializer()
        }
    }

    factoryOf(::LoginRepositoryImpl) { bind<LoginRepository>() }
    singleOf(::LoginUseCase)
    singleOf(::IsLoginUseCase)
    viewModelOf(::LoginViewModel)


    factoryOf(::DashboardRepositoryImpl) { bind<DashboardRepository>() }
    singleOf(::LogoutUseCase)
    singleOf(::FetchDashboardUseCase)
    viewModelOf(::DashboardViewModel)

    factoryOf(::MasterRepositoryImpl) { bind<MasterRepository>() }
    singleOf(::ListDosenPagingUseCase)
    viewModelOf(::ListDosenViewModel)
}

