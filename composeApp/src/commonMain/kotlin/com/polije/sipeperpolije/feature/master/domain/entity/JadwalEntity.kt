package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalItemUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalListUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DosenSummaryUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.UnscheduledItemUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalUI

data class JadwalEntity(
    val id: Int,
    val title: String,
    val isSuccess: Boolean,
    val jadwal: List<JadwalPerItemEntity>,
    val jadwalView: List<JadwalPerItemEntity>,
    val semester: String,
    val unscheduledCount: Int,
    val unscheduledItems: List<UnscheduledItemEntity>,
    val summary: List<DosenSummaryEntity>
)

data class UnscheduledItemEntity(
    val namaMk: String,
    val semester: Int,
    val sks: Int,
    val namaDosen: String,
    val pertemuanKe: Int,
    val alasan: String = "Tidak ada slot/ruangan yang tersedia",
    val degree: Int
)

data class DosenSummaryEntity(
    val namaDosen: String,
    val totalSesi: Int,
    val sksTeori: Int,
    val sksWorkshop: Int,
    val sksAjar: Int,
    val bebanSks: Int,
)

fun UnscheduledItemEntity.toUI() = UnscheduledItemUI(
    namaMk,
    semester,
    sks,
    namaDosen,
    pertemuanKe,
    alasan,
    degree
)

data class JadwalPerItemEntity(
    val nama: String,
    val items: List<JadwalItemEntity>
)

data class JadwalItemEntity(
    val sks: Int,
    val hari: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val semester: Int,
    val namaDosen: String,
    val namaJadwal: String,
    val namaRuangan: String,
    val namaTeknisi: String? = null
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
    namaTeknisi
)

fun DosenSummaryEntity.toUI() = DosenSummaryUI(
    namaDosen = namaDosen,
    totalSesi = totalSesi,
    sksTeori = sksTeori,
    sksWorkshop = sksWorkshop,
    sksAjar = sksAjar,
    bebanSks = bebanSks
)


fun JadwalEntity.toDetailUI() = DetailJadwalUI(
    isSuccess,
    jadwal.map { (nama, item) -> DetailJadwalListUI(nama, item.map { it.toUI() }) },
    jadwalView.map { (nama, item) -> DetailJadwalListUI(nama, item.map { it.toUI() }) },
    unscheduledCount = unscheduledCount,
    unscheduledItems = unscheduledItems.map { it.toUI() },
    summary = summary.map { it.toUI() }

)