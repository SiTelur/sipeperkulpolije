package com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.LogoutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val logoutUseCase: LogoutUseCase) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _events = Channel<DashboardEvent>()
    val events = _events.receiveAsFlow()

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

}

data class DashboardState(val isLogoutLoading: Boolean = false)
