package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel

sealed class DetailJadwalAction {
    data class OnDetailInitial(val id: Int) : DetailJadwalAction()
}