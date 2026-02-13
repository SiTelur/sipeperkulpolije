package com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel

sealed class ListJadwalAction {
    data class ChangeGenerationStatus(val status: Boolean?) : ListJadwalAction()
}
