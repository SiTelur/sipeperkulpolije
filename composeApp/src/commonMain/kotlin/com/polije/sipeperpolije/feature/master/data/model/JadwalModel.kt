package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalItemEntity

data class JadwalModel(
    val id: Int,
    val isSuccess: Boolean,
    val jadwal: Map<String, JadwalItemModel>,
    val semester: String
)

data class JadwalItemModel(
    val jam: String,
    val namaDosen: String,
    val namaJadwal: String
)

private fun JadwalItemModel.toEntity() = JadwalItemEntity(
    jam = jam, namaDosen = namaDosen, namaJadwal = namaJadwal
)

fun JadwalModel.toEntity() = JadwalEntity(
    id = id,
    isSuccess = isSuccess,
    semester = semester,
    jadwal = jadwal.mapValues { it.value.toEntity() })