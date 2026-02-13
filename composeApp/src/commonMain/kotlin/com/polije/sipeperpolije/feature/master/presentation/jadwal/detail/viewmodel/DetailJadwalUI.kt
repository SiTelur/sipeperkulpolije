package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel

import kotlinx.serialization.Serializable

data class DetailJadwalUI(
    val jadwal: Map<String, List<DetailJadwalItemUI>> = emptyMap(),
    val jadwalView: List<DetailJadwalListUI> = emptyList(),
)


@Serializable
data class DetailJadwalListUI(
    val nama: String,
    val item: List<DetailJadwalItemUI>
)

@Serializable
data class DetailJadwalItemUI(
    val jam: String,
    val namaDosen: String,
    val namaJadwal: String,
    val namaRuangan: String,
    val sks: Int,
    val semester: Int
)


