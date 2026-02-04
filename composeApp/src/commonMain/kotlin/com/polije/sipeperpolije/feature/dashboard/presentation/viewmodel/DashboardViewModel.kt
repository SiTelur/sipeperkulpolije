package com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.FetchDashboardUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.GenerateJadwalUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.LogoutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val fetchDashboardUseCase: FetchDashboardUseCase,

    private val generateJadwalUseCase: GenerateJadwalUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardEvent>()
    val events = _events.receiveAsFlow()

    init {
        fetchDashboard()
    }

    fun onAction(dashboardAction: DashboardAction) {
        when (dashboardAction) {
            is DashboardAction.OnLogoutPressed -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLogoutLoading = true)
                    runCatching {
                        logoutUseCase()
                    }.onSuccess {
                        _state.value = _state.value.copy(isLogoutLoading = false)
                        _events.send(DashboardEvent.LogoutSuccess)
                    }.onFailure {
                        _state.value = _state.value.copy(isLogoutLoading = false)
                        _events.send(DashboardEvent.LogoutFailed(it.message ?: "Unknown error"))
                    }
                }
            }

            is DashboardAction.OnGenerateJadwal -> {
                viewModelScope.launch {

                    generateJadwalUseCase(Semester.GANJIL)
                        .onSuccess {
                            if (it) {
                                _events.send(DashboardEvent.GenerateJadwalSuccess)
                            } else {
                                _events.send(DashboardEvent.GenerateJadwalFailed("Gagal generate jadwal"))
                            }
                        }
                        .onFailure {
                            _events.send(
                                DashboardEvent.GenerateJadwalFailed(
                                    it.message ?: "Unknown error"
                                )
                            )
                        }
                }
            }

        }
    }

    private fun fetchDashboard() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            fetchDashboardUseCase().onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    dosenCount = it.dosenActiveCount,
                    matkulCount = it.matkulActiveCount,
                    list = it.recentActivities.map { value -> value.toUI() }
                )
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false)
                _events.send(DashboardEvent.FetchDashboardFailed(it.message ?: "Unknown error"))
            }
        }
    }

}

data class DashboardState(
    val dosenCount: Int = 0,
    val matkulCount: Int = 0,
    val isLoading: Boolean = false,
    val isLogoutLoading: Boolean = false,
    val list: List<DashboardLog> = emptyList()
)
