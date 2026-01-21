package com.polije.sipeperpolije.feature.master.presentation.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity

data class DosenUI(val id: Int, val nama: String) {
    val initial = nama.first().toString()
}

fun DosenEntity.toUI() = DosenUI(this.id, this.nama)