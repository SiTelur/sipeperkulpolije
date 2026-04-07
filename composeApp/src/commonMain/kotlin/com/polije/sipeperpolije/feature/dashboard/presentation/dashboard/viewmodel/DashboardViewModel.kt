package com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.FetchDashboardUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.LogoutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val fetchDashboardUseCase: FetchDashboardUseCase,
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

        }
    }

    private fun fetchDashboard() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            fetchDashboardUseCase().onSuccess { value ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    dosenCount = value.dosenActiveCount,
                    totalGenerateJadwalCount = value.totalGenerateJadwalCount,
                    isLastGeneratedScheduleSuccess = value.isLastGeneratedScheduleSuccess,
                    matkulCount = value.matkulActiveCount,
                    list = value.recentActivities.map { value -> value.toUI() }
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
    val totalGenerateJadwalCount: Int = 0,
    val isLastGeneratedScheduleSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isLogoutLoading: Boolean = false,
    val list: List<DashboardLog> = emptyList()
)
