package com.polije.sipeperpolije.feature.master.presentation.settings.hari.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toUI
import com.polije.sipeperpolije.feature.master.domain.usecase.ListHariUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HariSettingsViewModel(private val listHariUseCase: ListHariUseCase) : ViewModel() {
    private val _settings = MutableStateFlow(SettingsState())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    init {
        fetchSettings()
    }

    private fun fetchSettings() {
        _settings.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            listHariUseCase().onSuccess { haris ->
                _settings.value =
                    SettingsState(isLoading = false, listOfHari = haris.map { it.toUI() })
            }.onFailure {
                _settings.value =
                    SettingsState(isLoading = false)
            }
        }
    }


}