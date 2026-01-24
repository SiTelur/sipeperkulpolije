package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

sealed class ListDosenEvent {
    class OnLoadError(message: String) : ListDosenEvent()
}