package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toDetailUI
import com.polije.sipeperpolije.feature.master.domain.usecase.GetDetailJadwalUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailJadwalViewModel(private val detailJadwalUsecase: GetDetailJadwalUseCase) : ViewModel() {
    private val _state = MutableStateFlow(DetailJadwalState(false, DetailJadwalUI()))
    val state = _state.asStateFlow()

    private val _channel = Channel<DetailJadwalEvent>()
    val channel = _channel.receiveAsFlow()


    fun onAction(action: DetailJadwalAction) {
        when (action) {
            is DetailJadwalAction.OnDetailInitial -> {
                _state.value = _state.value.copy(isLoading = true)
                viewModelScope.launch {
                    detailJadwalUsecase(action.id).onSuccess { value ->
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                jadwal = value.toDetailUI()
                            )
                        }
                        _channel.send(DetailJadwalEvent.OnSuccess)
                    }.onFailure {
                        _channel.send(DetailJadwalEvent.OnFailure(it.message.toString()))
                    }
                }
                _state.value = _state.value.copy(isLoading = false)
            }

        }
    }
}