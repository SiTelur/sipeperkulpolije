package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

sealed class GenerateJadwalEvent {
    data class PreviewJadwalFailed(val message: String) : GenerateJadwalEvent()

    data class GenerateJadwal(val id: Int, val status: Boolean) : GenerateJadwalEvent()
    object GenerateJadwalFailed : GenerateJadwalEvent()

}