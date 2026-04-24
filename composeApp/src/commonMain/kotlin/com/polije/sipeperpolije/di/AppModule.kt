package com.polije.sipeperpolije.di

import com.polije.sipeperpolije.BuildKonfig
import com.polije.sipeperpolije.feature.dashboard.data.repository.DashboardRepositoryImpl
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.FetchDashboardUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.GenerateJadwalUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.LogoutUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.PreviewJadwalUseCase
import com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel.DashboardViewModel
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.GenerateJadwalViewModel
import com.polije.sipeperpolije.feature.login.data.repository.LoginRepositoryImpl
import com.polije.sipeperpolije.feature.login.domain.repository.LoginRepository
import com.polije.sipeperpolije.feature.login.domain.usecase.IsLoginUseCase
import com.polije.sipeperpolije.feature.login.domain.usecase.LoginUseCase
import com.polije.sipeperpolije.feature.login.presentation.viewmodel.LoginViewModel
import com.polije.sipeperpolije.feature.master.data.repository.MasterRepositoryImpl
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteTeknisiUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.DetailDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.DownloadJadwalUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.GetDetailJadwalUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.GetDetailMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertTeknisiUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListDosenPagingUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListHariUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListJadwalUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListTeknisiUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.SearchDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateJamHariUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateTeknisiUseCase
import com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel.DetailDosenViewModel
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.ListDosenViewModel
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalViewModel
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalViewModel
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahViewModel
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.viewmodel.HariSettingsViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.EditRuanganViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel.EditTeknisiViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.viewmodel.SettingsViewModel
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
    factoryOf(::MasterRepositoryImpl) { bind<MasterRepository>() }
    factoryOf(::DashboardRepositoryImpl) { bind<DashboardRepository>() }

    singleOf(::LoginUseCase)
    singleOf(::IsLoginUseCase)
    viewModelOf(::LoginViewModel)

    singleOf(::PreviewJadwalUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::FetchDashboardUseCase)
    singleOf(::GenerateJadwalUseCase)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::GenerateJadwalViewModel)


    singleOf(::ListDosenPagingUseCase)
    viewModelOf(::ListDosenViewModel)

    singleOf(::ListMataKuliahUseCase)
    singleOf(::InsertMataKuliahUseCase)
    singleOf(::SearchDosenUseCase)
    singleOf(::GetDetailMataKuliahUseCase)
    singleOf(::UpdateMataKuliahUseCase)
    singleOf(::DeleteMataKuliahUseCase)
    viewModelOf(::ListMataKuliahViewModel)
    viewModelOf(::DetailMataKuliahViewModel)

    singleOf(::DetailDosenUseCase)
    singleOf(::UpdateDosenUseCase)
    singleOf(::DeleteDosenUseCase)
    singleOf(::InsertDosenUseCase)
    viewModelOf(::DetailDosenViewModel)

    singleOf(::ListJadwalUseCase)
    viewModelOf(::JadwalViewModel)
    singleOf(::GetDetailJadwalUseCase)
    singleOf(::DownloadJadwalUseCase)
    viewModelOf(::DetailJadwalViewModel)

    singleOf(::ListHariUseCase)
    singleOf(::UpdateJamHariUseCase)
    viewModelOf(::HariSettingsViewModel)
    viewModelOf(::SettingsViewModel)


    singleOf(::ListRuanganUseCase)
    singleOf(::InsertRuanganUseCase)
    singleOf(::UpdateRuanganUseCase)
    singleOf(::DeleteRuanganUseCase)
    viewModelOf(::EditRuanganViewModel)

    singleOf(::InsertTeknisiUseCase)
    singleOf(::UpdateTeknisiUseCase)
    singleOf(::DeleteTeknisiUseCase)
    singleOf(::ListTeknisiUseCase)
    viewModelOf(::EditTeknisiViewModel)

}

