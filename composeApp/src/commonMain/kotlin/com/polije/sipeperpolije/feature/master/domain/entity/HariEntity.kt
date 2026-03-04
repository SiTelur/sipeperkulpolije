package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.HariModel
import com.polije.sipeperpolije.feature.master.presentation.settings.hari.viewmodel.HariUI

data class HariEntity(
    val id: Int = 0,
    val nama: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val jamIstirahatMulai: Int?,
    val jamIstirahatSelesai: Int?
)

fun HariEntity.toUI() = HariUI(
    this.id,
    this.nama,
    this.jamMulai,
    this.jamSelesai,
    this.jamIstirahatMulai, jamIstirahatSelesai
)

fun HariEntity.toModel() = HariModel(
    id = id,
    nama = nama,
    jamMulai = jamMulai,
    jamSelesai = jamSelesai,
    jamIstirahatMulai = jamIstirahatMulai,
    jamIstirahatSelesai = jamIstirahatSelesai
)