package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

sealed class ListMataKuliahEvent {
    data class OnLoadError(val message: String) : ListMataKuliahEvent()
    data class OnSaveSuccess(val data: MataKuliahUI) : ListMataKuliahEvent()
    data class OnSaveFailure(val message: String) : ListMataKuliahEvent()
}