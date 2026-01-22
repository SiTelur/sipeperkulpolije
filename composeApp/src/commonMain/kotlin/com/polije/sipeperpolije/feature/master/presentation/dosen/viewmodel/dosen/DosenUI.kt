package com.polije.sipeperpolije.feature.master.presentation.dosen.viewmodel.dosen

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity

data class DosenUI(val id: Int, val nama: String, val initial: String) {

}

fun DosenEntity.toUI() = DosenUI(this.id, this.nama, nama.first().toString())