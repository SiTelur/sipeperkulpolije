package com.polije.sipeperpolije.feature.master.presentation.viewmodel.dosen

sealed class ListDosenEvent {
    class OnLoadError(message: String) : ListDosenEvent()
}