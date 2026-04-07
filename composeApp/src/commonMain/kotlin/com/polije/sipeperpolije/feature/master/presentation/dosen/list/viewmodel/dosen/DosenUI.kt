package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity

data class DosenUI(val id: Int = 0, val nama: String, val nidn: String) {
    val initial = nama.split(" ").take(2).map { it.first() }.joinToString("").uppercase()
}

fun DosenEntity.toUI() =
    DosenUI(
        this.id,
        this.nama,
        nidn
    )

fun DosenUI.toEntity() = DosenEntity(id, nama, nidn)