package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalItemEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalPerItemEntity
import com.polije.sipeperpolije.feature.master.domain.entity.UnscheduledItemEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JadwalModel(
    val id: Int,
    val title: String,
    @SerialName("is_success")
    val isSuccess: Boolean,
    @SerialName("jadwal")
    val listJadwal: List<JadwalPerItemModel> = emptyList(),
    @SerialName("jadwal_view")
    val listJadwalView: List<JadwalPerItemModel> = emptyList(),
    val semester: String,
    @SerialName("unscheduled_count")
    val unscheduledCount: Int = 0,
    @SerialName("unscheduled_items")
    val unscheduledItems: List<UnscheduledItemModel> = emptyList()
)

@Serializable
data class JadwalPerItemModel(
    val nama: String,
    val items: List<JadwalItemModel>
)

@Serializable
data class JadwalItemModel(
    val namaJadwal: String,
    val hari: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val namaDosen: String,
    val semester: Int, val namaRuangan: String, val sks: Int
)

private fun JadwalPerItemModel.toEntity() = JadwalPerItemEntity(
    nama = nama, items = items.map { it.toEntity() }
)

@Serializable
data class UnscheduledItemModel(
    @SerialName("nama_mk")
    val namaMk: String,
    val semester: Int,
    val sks: Int,
    @SerialName("nama_dosen")
    val namaDosen: String,
    @SerialName("pertemuan_ke")
    val pertemuanKe: Int,
    val alasan: String = "Tidak ada slot/ruangan yang tersedia",
    val degree: Int
)

fun UnscheduledItemModel.toEntity() = UnscheduledItemEntity(
    namaMk, semester, sks, namaDosen, pertemuanKe, alasan, degree
)

private fun JadwalItemModel.toEntity() = JadwalItemEntity(
    jamMulai = jamMulai,
    jamSelesai = jamSelesai,
    namaDosen = namaDosen,
    namaJadwal = namaJadwal,
    semester = semester, hari = hari, namaRuangan = namaRuangan, sks = sks
)

fun JadwalModel.toEntity() = JadwalEntity(
    id = id,
    title = title,
    isSuccess = isSuccess,
    semester = semester,
    jadwal = listJadwal.map { it.toEntity() },
    jadwalView = listJadwalView.map { it.toEntity() },
    unscheduledCount = unscheduledCount,
    unscheduledItems = unscheduledItems.map { it.toEntity() }
)
