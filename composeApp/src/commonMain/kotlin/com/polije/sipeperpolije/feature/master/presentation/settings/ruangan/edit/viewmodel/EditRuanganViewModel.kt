package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toUI
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListRuanganUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateRuanganUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditRuanganViewModel(
    private val listRuanganUseCase: ListRuanganUseCase,
    private val insertRuanganUseCase: InsertRuanganUseCase,
    private val updateRuanganUseCase: UpdateRuanganUseCase,
    private val deleteRuanganUseCase: DeleteRuanganUseCase
) : ViewModel() {
    private val _ruangan: MutableStateFlow<EditRuanganState> = MutableStateFlow(EditRuanganState())
    val ruangan: StateFlow<EditRuanganState> = _ruangan.asStateFlow()

    init {
        loadItems()
    }

    private val _events = Channel<RuanganEvent>()
    val events = _events.receiveAsFlow()


    private fun loadItems() {
        viewModelScope.launch {
            listRuanganUseCase().onSuccess { ruangans ->
                _ruangan.update { state -> state.copy(listRuangan = ruangans.map { it.toUI() }) }
            }.onFailure {
                _events.send(RuanganEvent.OnFailure(it.message ?: "Terjadi error"))
            }

        }
    }

    fun onAction(editRuanganAction: RuanganAction) {
        when (editRuanganAction) {
            RuanganAction.OnDismissRuangan -> {
                _ruangan.update { ruangan ->
                    ruangan.copy(selectedRuangan = null)
                }
            }

            is RuanganAction.OnRuanganSelected -> {
                _ruangan.update { ruangan ->
                    ruangan.copy(selectedRuangan = editRuanganAction.ruangan)
                }
            }

            is RuanganAction.OnUpdateRuangan -> {
                viewModelScope.launch {
                    updateRuanganUseCase(editRuanganAction.ruangan.toEntity()).onSuccess {
                        _events.send(RuanganEvent.OnUpdateSuccess)
                        loadItems()
                    }.onFailure {
                        _events.send(RuanganEvent.OnFailure(it.message ?: "Terjadi error"))
                    }
                }
            }

            is RuanganAction.OnDeleteRuangan -> {
                viewModelScope.launch {
                    deleteRuanganUseCase(editRuanganAction.id).onSuccess {
                        _events.send(RuanganEvent.OnDeleteSuccess)
                        loadItems()
                    }.onFailure {
                        _events.send(RuanganEvent.OnFailure(it.message ?: "Terjadi error"))
                    }
                }
            }

            is RuanganAction.OnInsertRuangan -> {
                viewModelScope.launch {
                    insertRuanganUseCase(editRuanganAction.ruangan.toEntity()).onSuccess {
                        _events.send(RuanganEvent.OnInsertSuccess)
                        loadItems()
                    }.onFailure {
                        _events.send(RuanganEvent.OnFailure(it.message ?: "Terjadi error"))
                    }
                }
            }
        }
    }
}


