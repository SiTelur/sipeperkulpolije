package com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toUI
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
                .onSuccess { value ->
                    _state.update { jadwalState ->
                        jadwalState.copy(
                            isLoading = false,
                            jadwal = value.map { it.toUI() },
                            filteredJadwal = value.map { it.toUI() })
                    }

                }.onFailure {

                }
        }
    }

    fun onAction(action: ListJadwalAction) {
        when (action) {
            is ListJadwalAction.ChangeGenerationStatus -> {
                val currentList = _state.value.jadwal

                if (action.status == null) {
                    _state.update { it.copy(jadwal = currentList) }
                    return
                }

                _state.update {
                    it.copy(filteredJadwal = currentList.filter { list -> list.isSuccess == action.status })
                }

            }
        }
    }
}