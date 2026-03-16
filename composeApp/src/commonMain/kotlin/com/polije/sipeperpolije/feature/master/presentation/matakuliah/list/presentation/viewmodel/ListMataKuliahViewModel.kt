package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.SearchDosenUseCase
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.toUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahEvent.OnSaveFailure
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahEvent.OnSaveSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListMataKuliahViewModel(
    private val listMataKuliahUseCase: ListMataKuliahUseCase,
    private val insertMataKuliahUseCase: InsertMataKuliahUseCase,
    val searchDosenUseCase: SearchDosenUseCase
) :
    ViewModel() {
    private val _state = MutableStateFlow(ListMataKuliahState())
    val state = _state.asStateFlow()
    private val _event = Channel<ListMataKuliahEvent>()
    val events = _event.receiveAsFlow()

    init {
        loadItems()
        initiateDosenList()
    }

    fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            listMataKuliahUseCase().onSuccess { mataKuliah ->
                _state.update { it ->
                    it.copy(
                        mataKuliahs = mataKuliah.map { it.toUI() },
                        filteredMatakuliahs = mataKuliah.map { it.toUI() },
                        isLoading = false
                    )
                }
            }.onFailure {
                _event.send(ListMataKuliahEvent.OnLoadError(it.message ?: "Terjadi error"))
            }
        }
    }


    private fun initiateDosenList() {
        viewModelScope.launch {
            searchDosenUseCase().onSuccess { response ->
                _state.update { it.copy(result = response.map { value -> value.toUI() }) }
            }
        }
    }

    fun onAction(listMataKuliahAction: ListMataKuliahAction) {
        when (listMataKuliahAction) {
            is ListMataKuliahAction.OnSaveMataKuliah -> {
                viewModelScope.launch {
                    insertMataKuliahUseCase(
                        MataKuliahUI(
                            nama = listMataKuliahAction.nama,
                            kode = listMataKuliahAction.kode,
                            semester = listMataKuliahAction.semester,
                            sksTeori = listMataKuliahAction.sksTeori,
                            sksPraktek = listMataKuliahAction.sksPraktek,
                            idPengampu = listMataKuliahAction.dosenID,
                            namaPenampu = listMataKuliahAction.namaDosen
                                ?: "Belum ditentukan",
                            isActive = listMataKuliahAction.isActive
                        ).toEntity()
                    ).onSuccess {
                        _event.send(OnSaveSuccess(it.toUI()))
                        loadItems()
                    }.onFailure {
                        _event.send(
                            OnSaveFailure(
                                it.message ?: "Terjadi error"
                            )
                        )
                    }
                }
            }

            is ListMataKuliahAction.OnSearcDosen -> {
                _state.update { state ->
                    state.copy(
                        filteredMatakuliahs = state.filteredMatakuliahs.filter {
                            it.nama.contains(
                                listMataKuliahAction.query,
                                ignoreCase = true
                            ) || it.kode.contains(
                                listMataKuliahAction.query,
                                ignoreCase = true
                            ) || it.semester == (listMataKuliahAction.query.toIntOrNull()
                                ?: "") || it.namaPenampu.contains(listMataKuliahAction.query, true)
                        }
                    )
                }
            }

            is ListMataKuliahAction.OnChangeStatusChip -> {
                _state.update { state ->
                    val currentList = state.mataKuliahs

                    val filtered = listMataKuliahAction.status?.let { status ->
                        currentList.filter { it.isActive == status }
                    } ?: currentList

                    state.copy(filteredMatakuliahs = filtered)
                }
            }
        }
    }
}