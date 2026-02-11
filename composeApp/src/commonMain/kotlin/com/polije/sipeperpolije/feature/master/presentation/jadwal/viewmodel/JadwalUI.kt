package com.polije.sipeperpolije.feature.master.presentation.jadwal.viewmodel

data class JadwalUI(
    val id: Int,
    val isSuccess: Boolean,
    val jadwal: Map<String, List<JadwalItemUI>>,
    val semester: String
)

data class JadwalItemUI(
    val jam: String,
    val namaDosen: String,
    val semester: String
)

