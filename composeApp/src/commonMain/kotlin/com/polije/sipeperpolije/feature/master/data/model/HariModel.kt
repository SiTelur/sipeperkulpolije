package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.HariEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HariModel(
    val id: Int,
    @SerialName("jam_mulai")
    val jamMulai: Int,
    @SerialName("jam_selesai")
    val jamSelesai: Int,
    @SerialName("jam_istirahat_mulai")
    val jamIstirahatMulai: Int?,
    @SerialName("jam_istirahat_selesai")
    val jamIstirahatSelesai: Int?
)

fun HariModel.toEntity() = HariEntity(
    this.id,
    this.jamMulai,
    this.jamSelesai,
    this.jamIstirahatMulai,
    this.jamIstirahatSelesai

)