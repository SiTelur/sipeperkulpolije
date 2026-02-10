package com.polije.sipeperpolije.feature.master.presentation.jadwal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.ListJadwalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JadwalViewModel(private val listJadwalUseCase: ListJadwalUseCase) : ViewModel() {
    private val _state = MutableStateFlow(ListJadwalState())
    val state = _state.asStateFlow()

    init {
        fetchJadwal()
    }

    fun fetchJadwal() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            listJadwalUseCase()
                .onSuccess {
                    _state.update { it.copy(isLoading = false, jadwal = it.jadwal) }
                }.onFailure {

                }
        }
    }
}