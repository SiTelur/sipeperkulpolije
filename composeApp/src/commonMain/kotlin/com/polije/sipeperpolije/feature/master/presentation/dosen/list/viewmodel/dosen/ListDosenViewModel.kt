package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListDosenPagingUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListDosenViewModel(
    private val listDosenPagingUseCase: ListDosenPagingUseCase,
    private val insertDosenUseCase: InsertDosenUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ListDosenState())
    val state = _state.asStateFlow()

    private val _event = Channel<ListDosenEvent>()
    val events = _event.receiveAsFlow()

    fun onAction(action: ListDosenAction) {
        when (action) {
            is ListDosenAction.InsertDosen -> {
                viewModelScope.launch {
                    insertDosenUseCase(
                        DosenUI(
                            nama = action.nama,
                            nidn = action.nidn,
                            isActive = action.isActive,
                            tipeDosen = action.tipeDosen
                        )
                    ).onSuccess {
                        _event.send(ListDosenEvent.OnSaveSuccess)
                    }.onFailure {
                        _event.send(ListDosenEvent.OnSaveError(it.message ?: "Terjadi error"))
                    }
                }
            }

            is ListDosenAction.OnInitial -> {
                loadItems()
            }
        }
    }


    init {
        _state.update { it.copy(dosenGrouped = emptyList()) }
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            listDosenPagingUseCase().onSuccess { list ->
                _state.update { state ->
                    state.copy(dosenGrouped = list.map { mapDosen ->
                        DosenGrouped(
                            mapDosen.key,
                            mapDosen.value.map { it.toUI() }
                        )
                    })
                }
            }.onFailure {
                _event.send(ListDosenEvent.OnLoadError(it.message ?: "Terjadi error"))
            }
        }
    }
}