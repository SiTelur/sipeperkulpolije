package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity

data class DosenUI(val id: Int, val nama: String, val initial: String, val nidn: String)

fun DosenEntity.toUI() =
    DosenUI(
        this.id,
        this.nama,
        nama.split(" ").take(2).map { it.first() }.joinToString("").uppercase(),
        nidn
    )