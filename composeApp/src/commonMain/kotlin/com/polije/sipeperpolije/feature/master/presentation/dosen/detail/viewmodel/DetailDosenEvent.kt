package com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel

sealed class DetailDosenEvent {
    object OnDosenSuccessAction : DetailDosenEvent()
    data class OnDosenFailedAction(val message: String) : DetailDosenEvent()
}