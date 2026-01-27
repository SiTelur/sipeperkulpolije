package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListMataKuliahPagingUseCase
import com.polije.sipeperpolije.utils.Paginator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListMataKuliahViewModel(
    private val listMataKuliahPagingUseCase: ListMataKuliahPagingUseCase,
    private val insertMataKuliahUseCase: InsertMataKuliahUseCase
) :
    ViewModel() {
    private val _state = MutableStateFlow(ListMataKuliahState())
    val state = _state.asStateFlow()

    private val _event = Channel<ListMataKuliahEvent>()
    val events = _event.receiveAsFlow()

    private val pageSize = 10
    private val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { currentKey ->
            listMataKuliahPagingUseCase(
                currentKey,
                pageSize
            ).map { value -> value.map { it.toUI() } }
        },
        getNextKey = { currentKey, result ->
            currentKey + result.size
        },
        onError = { throwable ->
            _state.update { it.copy(hasError = throwable?.message) }
//            _event.send(ListDosenEvent.OnLoadError(throwable?.message ?: "Terjadi error"))
        },
        onSuccess = { items, newKey ->
            _state.update {
                it.copy(
                    mataKuliahs = it.mataKuliahs + items
                )
            }

        }, endReached = { _, response -> response.isEmpty() }
    )

    init {
        loadNextItems()
    }

    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun resetItems() {
        viewModelScope.launch {
            paginator.reset()
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
                            jumlahSKS = listMataKuliahAction.sks,
                            idPengampu = null,
                            namaPenampuPertama = ""
                        ).toEntity()
                    ).onSuccess {
                        _event.send(ListMataKuliahEvent.OnSaveSuccess(it.toUI()))
                    }.onFailure {
                        _event.send(
                            ListMataKuliahEvent.OnSaveFailure(
                                it.message ?: "Terjadi error"
                            )
                        )
                    }
                }
            }
        }
    }
}