package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.InsertDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.ListDosenPagingUseCase
import com.polije.sipeperpolije.utils.Paginator
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
                    insertDosenUseCase(DosenUI(nama = action.nama, nidn = action.nidn)).onSuccess {
                        _event.send(ListDosenEvent.OnSaveSuccess)
                    }.onFailure {
                        _event.send(ListDosenEvent.OnSaveError(it.message ?: "Terjadi error"))
                    }
                }
            }
        }
    }

    private val pageSize = 10
    private val paginator = Paginator(
        initialKey = 0,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { currentKey ->
            listDosenPagingUseCase(
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
                    dosens = it.dosens + items
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
            _state.update { it.copy(dosens = emptyList()) }
        }
    }
}