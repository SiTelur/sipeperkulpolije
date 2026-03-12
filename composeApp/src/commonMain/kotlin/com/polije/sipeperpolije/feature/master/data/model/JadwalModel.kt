package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalItemEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalPerItemEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JadwalModel(
    val id: Int,
    val title: String,
    @SerialName("is_success")
    val isSuccess: Boolean,
    val jadwal: List<JadwalPerItemModel> = emptyList(),
    @SerialName("jadwal_view")
    val listJadwalView: List<JadwalPerItemModel> = emptyList(),
    val semester: String
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
    jadwal = jadwal.map { entry ->
        entry.toEntity()
    }, jadwalView = listJadwalView.map { entry -> entry.toEntity() })
