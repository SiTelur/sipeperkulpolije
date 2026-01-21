package com.polije.sipeperpolije.feature.master.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.polije.sipeperpolije.feature.master.domain.usecase.ListDosenPagingUseCase
import kotlinx.coroutines.flow.map

class ListDosenViewModel(val listDosenPagingUseCase: ListDosenPagingUseCase) : ViewModel() {
    val dosenPaging =
        listDosenPagingUseCase().map { data -> data.map { dosenEntity -> dosenEntity.toUI() } }
            .cachedIn(viewModelScope)
}