package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalItemUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalListUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalUI

data class JadwalEntity(
    val id: Int,
    val isSuccess: Boolean,
    val jadwal: Map<String, List<JadwalItemEntity>>,
    val jadwalView: Map<String, List<JadwalItemEntity>>,
    val semester: String
)

data class JadwalItemEntity(
    val jamMulai: Int,
    val jamSelesai: Int,
    val hari: String,
    val namaDosen: String,
    val namaJadwal: String,
    val semester: Int,
    val namaRuangan: String,
    val sks: Int
)

fun JadwalEntity.toUI() = JadwalUI(
    id,
    isSuccess,
    semester
)

fun JadwalItemEntity.toUI() = DetailJadwalItemUI(
    jam = "$jamMulai:00 - $jamSelesai:00",
    namaDosen,
    namaJadwal,
    namaRuangan,
    sks,
    semester,
)


fun JadwalEntity.toDetailUI() = DetailJadwalUI(

    jadwal.mapValues { it.value.map { value -> value.toUI() } },
    jadwalView.map { (key, value) -> DetailJadwalListUI(key, value.map { it.toUI() }) },

    )