package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel

sealed class DetailJadwalEvent {
    object OnSuccess : DetailJadwalEvent()
    data class OnFailure(val message: String) : DetailJadwalEvent()

    object OnSuccessDownloadJadwal : DetailJadwalEvent()
    data class OnFailureDownloadJadwal(val message: String) : DetailJadwalEvent()
}