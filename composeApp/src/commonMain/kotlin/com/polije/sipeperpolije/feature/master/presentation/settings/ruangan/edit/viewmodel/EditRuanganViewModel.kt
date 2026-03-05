package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.entity.toUI
import com.polije.sipeperpolije.feature.master.domain.usecase.ListRuanganUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditRuanganViewModel(private val listRuanganUseCase: ListRuanganUseCase) : ViewModel() {
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
                _events.send(RuanganEvent.OnUpdateFailure(it.message ?: "Terjadi error"))
            }

        }
    }

    fun onAction(editRuanganAction: RuanganAction) {
        when (editRuanganAction) {
            RuanganAction.OnDismissRuangan -> {}
            is RuanganAction.OnRuanganSelected -> {}
            is RuanganAction.OnUpdateRuangan -> {}
        }
    }
}


