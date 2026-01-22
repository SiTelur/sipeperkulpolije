package com.polije.sipeperpolije.feature.master.presentation.matakuliah.presentation.viewmodel

sealed class ListMataKuliahEvent {
    class OnLoadError(message: String) : ListMataKuliahEvent()
}