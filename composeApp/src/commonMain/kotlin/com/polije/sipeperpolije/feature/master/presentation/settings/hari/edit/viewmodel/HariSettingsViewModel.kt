package com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toUI
import com.polije.sipeperpolije.feature.master.domain.usecase.ListHariUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HariSettingsViewModel(private val listHariUseCase: ListHariUseCase) : ViewModel() {
    private val _settings = MutableStateFlow(HariState())
    val settings: StateFlow<HariState> = _settings.asStateFlow()

    private val _action = Channel<HariAction>()
    val action = _action.receiveAsFlow()

    init {
        fetchSettings()
    }

    private fun fetchSettings() {
        _settings.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            listHariUseCase().onSuccess { haris ->
                _settings.value =
                    HariState(isLoading = false, listOfHari = haris.map { it.toUI() })
            }.onFailure {
                _settings.value =
                    HariState(isLoading = false)
            }
        }
    }

    fun onAction(action: HariAction) {
        when (action) {
            is HariAction.OnHariSelected -> {
                _settings.update { it.copy(selectedHari = action.hari) }
            }

            HariAction.OnDismissHari -> {
                _settings.update { it.copy(selectedHari = null) }
            }
        }
    }
}