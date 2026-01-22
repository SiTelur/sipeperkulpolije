package com.polije.sipeperpolije.feature.master.presentation.dosen.viewmodel.dosen

sealed class ListDosenEvent {
    class OnLoadError(message: String) : ListDosenEvent()
}