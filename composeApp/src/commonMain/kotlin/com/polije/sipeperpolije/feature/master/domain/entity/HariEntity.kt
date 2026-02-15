package com.polije.sipeperpolije.feature.master.domain.entity

data class HariEntity(
    val id: Int,
    val jamMulai: Int,
    val jamSelesai: Int,
    val jamIstirahatMulai: Int?,
    val jamIstirahatSelesai: Int?
)