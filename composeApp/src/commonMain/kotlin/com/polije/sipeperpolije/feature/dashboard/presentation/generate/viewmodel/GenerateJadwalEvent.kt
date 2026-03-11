package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

sealed class GenerateJadwalEvent {
    data class PreviewJadwalFailed(val message: String) : GenerateJadwalEvent()
}