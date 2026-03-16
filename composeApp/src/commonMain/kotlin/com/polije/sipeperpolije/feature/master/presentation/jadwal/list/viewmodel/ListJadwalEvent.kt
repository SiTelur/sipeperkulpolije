package com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel

sealed class ListJadwalEvent {
    data class OnFailure(val message: String) : ListJadwalEvent()
}