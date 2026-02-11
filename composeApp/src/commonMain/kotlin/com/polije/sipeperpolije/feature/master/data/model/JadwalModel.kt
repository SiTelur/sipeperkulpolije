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
    val jadwal: Map<String, List<JadwalItemModel>>,
    val semester: String
)

@Serializable
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
    jadwal = jadwal.mapValues { entry ->
        entry.value.map { it.toEntity() }
    })