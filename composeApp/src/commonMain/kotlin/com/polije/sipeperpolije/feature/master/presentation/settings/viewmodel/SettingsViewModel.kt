package com.polije.sipeperpolije.feature.master.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toUI
import com.polije.sipeperpolije.feature.master.domain.usecase.ListHariUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val listHariUseCase: ListHariUseCase) : ViewModel() {
    private val _settings = MutableStateFlow(SettingsState())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    init {
        fetchSettings()
    }

    private fun fetchSettings() {
        _settings.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            listHariUseCase().onSuccess {
                _settings.value =
                    SettingsState(isLoading = false, listOfHari = it.map { it.toUI() })
            }.onFailure {
                _settings.value =
                    SettingsState(isLoading = false)
            }
        }
    }


}