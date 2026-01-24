package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

sealed class ListMataKuliahEvent {
    class OnLoadError(message: String) : ListMataKuliahEvent()
}