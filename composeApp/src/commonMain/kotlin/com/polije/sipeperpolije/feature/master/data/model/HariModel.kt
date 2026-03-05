package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.HariEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HariModel(
    val id: Int = 0,
    val nama: String,
    @SerialName("jam_mulai")
    val jamMulai: Int,
    @SerialName("jam_selesai")
    val jamSelesai: Int,
    @SerialName("jam_mulai_istirahat")
    val jamMulaiIstirahat: Int?,
    @SerialName("jam_selesai_istirahat")
    val jamSelesaiIstirahat: Int?
)

fun HariModel.toEntity() = HariEntity(
    this.id,
    this.nama,
    this.jamMulai,
    this.jamSelesai,
    this.jamMulaiIstirahat,
    this.jamSelesaiIstirahat

)