package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalItemUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalUI

data class JadwalEntity(
    val id: Int,
    val isSuccess: Boolean,
    val jadwal: Map<String, List<JadwalItemEntity>>,
    val semester: String
)

data class JadwalItemEntity(
    val jam: String,
    val namaDosen: String,
    val namaJadwal: String
)

private fun JadwalItemEntity.toUI() = JadwalItemUI(
    jam = jam, namaDosen = namaDosen, semester = namaJadwal
)

fun JadwalEntity.toUI() = JadwalUI(
    id, isSuccess, jadwal.mapValues { it -> it.value.map { it.toUI() } }, semester
)