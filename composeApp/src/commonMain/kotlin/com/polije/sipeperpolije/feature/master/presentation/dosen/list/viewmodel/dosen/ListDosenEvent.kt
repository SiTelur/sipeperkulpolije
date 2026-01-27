package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

sealed class ListDosenEvent {
    data class OnLoadError(val message: String) : ListDosenEvent()

    data class OnSaveError(val message: String) : ListDosenEvent()
    object OnSaveSuccess : ListDosenEvent()

}