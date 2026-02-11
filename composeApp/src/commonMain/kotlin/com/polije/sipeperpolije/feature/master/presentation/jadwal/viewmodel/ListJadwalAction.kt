package com.polije.sipeperpolije.feature.master.presentation.jadwal.viewmodel

sealed class ListJadwalAction {
    data class ChangeGenerationStatus(val status: Boolean?) : ListJadwalAction()
}
