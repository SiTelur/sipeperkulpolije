package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.DosenSummaryEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalItemEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalPerItemEntity
import com.polije.sipeperpolije.feature.master.domain.entity.TeknisiSummaryEntity
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
    val unscheduledItems: List<UnscheduledItemModel> = emptyList(),
    val summary: List<DosenSummaryModel> = emptyList(),
    @SerialName("teknisi_summary") val teknisiSummary: List<TeknisiSummary> = emptyList()  // ← tambahkan ini
)

@Serializable
data class DosenSummaryModel(
    @SerialName("nama_dosen") val namaDosen: String,
    @SerialName("total_sesi") val totalSesi: Int,
    @SerialName("sks_teori") val sksTeori: Int,
    @SerialName("sks_workshop") val sksWorkshop: Int,
    @SerialName("sks_ajar") val sksAjar: Int,
    @SerialName("beban_sks") val bebanSks: Int
)

@Serializable
data class JadwalPerItemModel(
    val nama: String,
    val items: List<JadwalItemModel>
)

@Serializable
data class JadwalItemModel(
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

@Serializable
data class TeknisiSummary(
    @SerialName("nama_teknisi") val namaTeknisi: String,
    @SerialName("total_sesi") val totalSesi: Int,
    @SerialName("beban_sks") val bebanSks: Int
)

fun TeknisiSummary.toEntity() = TeknisiSummaryEntity(
    namaTeknisi, totalSesi, bebanSks
)

private fun JadwalItemModel.toEntity() = JadwalItemEntity(
    jamMulai = jamMulai,
    jamSelesai = jamSelesai,
    namaDosen = namaDosen,
    namaJadwal = namaJadwal,
    semester = semester,
    hari = hari,
    namaRuangan = namaRuangan,
    sks = sks,
    namaTeknisi = namaTeknisi
)

private fun DosenSummaryModel.toEntity() = DosenSummaryEntity(
    namaDosen = namaDosen,
    totalSesi = totalSesi,
    sksTeori = sksTeori,
    sksWorkshop = sksWorkshop,
    sksAjar = sksAjar,
    bebanSks = bebanSks
)


fun JadwalModel.toEntity() = JadwalEntity(
    id = id,
    title = title,
    isSuccess = isSuccess,
    semester = semester,
    jadwal = listJadwal.map { it.toEntity() },
    jadwalView = listJadwalView.map { it.toEntity() },
    unscheduledCount = unscheduledCount,
    unscheduledItems = unscheduledItems.map { it.toEntity() },
    summary = summary.map { it.toEntity() },
    teknisiSummary = teknisiSummary.map { it.toEntity() }
)
