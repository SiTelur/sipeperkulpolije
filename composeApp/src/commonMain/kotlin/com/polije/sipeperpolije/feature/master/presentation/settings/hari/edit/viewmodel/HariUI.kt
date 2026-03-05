package com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.HariEntity

class HariUI(
    val id: Int,
    val nama: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val jamIstirahatMulai: Int?,
    val jamIstirahatSelesai: Int?
)

fun HariUI.toEntity(): HariEntity =
    HariEntity(id, nama, jamMulai, jamSelesai, jamIstirahatMulai, jamIstirahatSelesai)