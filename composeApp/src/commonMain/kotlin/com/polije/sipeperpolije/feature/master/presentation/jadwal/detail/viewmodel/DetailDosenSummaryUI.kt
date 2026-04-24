package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel

data class DetailDosenSummaryUI(
    val namaDosen: String,
    val totalSks: Int,
    val totalSesi: Int,
    val sksTeori: Int,
    val sksWorkshop: Int,
    val sksAjar: Int
)
