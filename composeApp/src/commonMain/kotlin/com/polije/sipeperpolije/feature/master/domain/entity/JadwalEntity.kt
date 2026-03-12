package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalItemUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalListUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalUI

data class JadwalEntity(
    val id: Int,
    val title: String,
    val isSuccess: Boolean,
    val jadwal: List<JadwalPerItemEntity>,
    val jadwalView: List<JadwalPerItemEntity>,
    val semester: String,
)

data class JadwalPerItemEntity(
    val nama: String,
    val items: List<JadwalItemEntity>
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
    title,
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
    jadwal.map { (nama, item) -> DetailJadwalListUI(nama, item.map { it.toUI() }) },
    jadwalView.map { (nama, item) -> DetailJadwalListUI(nama, item.map { it.toUI() }) },
)