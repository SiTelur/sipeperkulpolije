package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.SearchDosenUseCase
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailMataKuliahViewModel(private val searchDosenUseCase: SearchDosenUseCase) : ViewModel() {

    private val _state = MutableStateFlow(DetailMataKuliahState())
    val state = _state.asStateFlow()

    init {
        initiateDosenList()
    }

    private fun initiateDosenList() {
        viewModelScope.launch {
            searchDosenUseCase().onSuccess { response ->
                _state.update { it.copy(result = response.map { value -> value.toUI() }) }
            }
        }
    }
}