package com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateDosenUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailDosenViewModel(
    private val updateDosenUseCase: UpdateDosenUseCase,
    private val deleteDosenUseCase: DeleteDosenUseCase
) : ViewModel() {

    fun onAction(detailDosenAction: DetailDosenAction) {
        when (detailDosenAction) {
            is DetailDosenAction.OnDosenUpdate -> {
                viewModelScope.launch {
                    updateDosenUseCase(detailDosenAction.dosenUI).onSuccess {
                        _event.send(
                            DetailDosenEvent.OnDosenSuccessAction
                        )
                    }.onFailure {
                        _event.send(
                            DetailDosenEvent.OnDosenFailedAction(
                                it.message ?: "Unknown Error"
                            )
                        )
                    }
                }
            }

            is DetailDosenAction.OnDosenDelete -> {
                viewModelScope.launch {
                    deleteDosenUseCase(detailDosenAction.id).onSuccess {
                        _event.send(
                            DetailDosenEvent.OnDosenSuccessAction
                        )
                    }.onFailure {
                        _event.send(
                            DetailDosenEvent.OnDosenFailedAction(
                                it.message ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    private val _event = Channel<DetailDosenEvent>()
    val events = _event.receiveAsFlow()
}