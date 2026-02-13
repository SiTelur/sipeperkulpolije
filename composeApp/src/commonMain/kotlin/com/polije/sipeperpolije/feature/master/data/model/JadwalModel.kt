package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalItemEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JadwalModel(
    val id: Int,
    @SerialName("is_success")
    val isSuccess: Boolean,
    val jadwal: Map<String, List<JadwalItemModel>> = emptyMap(),
    @SerialName("jadwal_view")
    val listJadwalView: Map<String, List<JadwalItemModel>> = emptyMap(),
    val semester: String
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

private fun JadwalItemModel.toEntity() = JadwalItemEntity(
    jamMulai = jamMulai,
    jamSelesai = jamSelesai,
    namaDosen = namaDosen,
    namaJadwal = namaJadwal,
    semester = semester, hari = hari, namaRuangan = namaRuangan, sks = sks
)

fun JadwalModel.toEntity() = JadwalEntity(
    id = id,
    isSuccess = isSuccess,
    semester = semester,
    jadwal = jadwal.mapValues { entry ->
        entry.value.map { it.toEntity() }
    }, jadwalView = jadwal.mapValues { entry -> entry.value.map { it.toEntity() } })
