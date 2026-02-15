package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.presentation.settings.viewmodel.HariUI

data class HariEntity(
    val id: Int,
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