package com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel

data class ListJadwalState(
    val isLoading: Boolean = false,
    val jadwal: List<JadwalUI> = emptyList(),
    val filteredJadwal: List<JadwalUI> = emptyList()
)
