package com.polije.sipeperpolije.feature.master.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.LogoutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val logoutUseCase: LogoutUseCase) : ViewModel() {

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnLogoutPressed -> {
                viewModelScope.launch {
                    try {
                        logoutUseCase()
                        _events.send(SettingsEvent.LogoutSuccess)
                    } catch (e: Exception) {
                        _events.send(SettingsEvent.LogoutFailed(e.message.toString()))
                    }
                }
            }

        }
    }


}