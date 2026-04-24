package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteTeknisiUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertTeknisiUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListTeknisiUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateTeknisiUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditTeknisiViewModel(
    private val listTeknisiUseCase: ListTeknisiUseCase,
    private val insertTeknisiUseCase: InsertTeknisiUseCase,
    private val updateTeknisiUseCase: UpdateTeknisiUseCase,
    private val deleteTeknisiUseCase: DeleteTeknisiUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<EditTeknisiState> = MutableStateFlow(EditTeknisiState())
    val state: StateFlow<EditTeknisiState> = _state.asStateFlow()

    init {
        loadItems()
    }

    private val _events = Channel<TeknisiEvent>()
    val events = _events.receiveAsFlow()

    private fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            listTeknisiUseCase().onSuccess { teknisis ->
                _state.update { state -> state.copy(listTeknisi = teknisis.map { it.toUI() }, isLoading = false) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
                _events.send(TeknisiEvent.OnFailure(it.message ?: "Terjadi error"))
            }
        }
    }

    fun onAction(action: TeknisiAction) {
        when (action) {
            TeknisiAction.OnDismissTeknisi -> {
                _state.update { it.copy(selectedTeknisi = null) }
            }
            is TeknisiAction.OnTeknisiSelected -> {
                _state.update { it.copy(selectedTeknisi = action.teknisi) }
            }
            is TeknisiAction.OnUpdateTeknisi -> {
                viewModelScope.launch {
                    updateTeknisiUseCase(action.teknisi.toEntity()).onSuccess {
                        _events.send(TeknisiEvent.OnUpdateSuccess)
                        loadItems()
                    }.onFailure {
                        _events.send(TeknisiEvent.OnFailure(it.message ?: "Terjadi error"))
                    }
                }
            }
            is TeknisiAction.OnDeleteTeknisi -> {
                viewModelScope.launch {
                    deleteTeknisiUseCase(action.id).onSuccess {
                        _events.send(TeknisiEvent.OnDeleteSuccess)
                        loadItems()
                    }.onFailure {
                        _events.send(TeknisiEvent.OnFailure(it.message ?: "Terjadi error"))
                    }
                }
            }
            is TeknisiAction.OnInsertTeknisi -> {
                viewModelScope.launch {
                    insertTeknisiUseCase(action.teknisi.toEntity()).onSuccess {
                        _events.send(TeknisiEvent.OnInsertSuccess)
                        loadItems()
                    }.onFailure {
                        _events.send(TeknisiEvent.OnFailure(it.message ?: "Terjadi error"))
                    }
                }
            }
        }
    }
}