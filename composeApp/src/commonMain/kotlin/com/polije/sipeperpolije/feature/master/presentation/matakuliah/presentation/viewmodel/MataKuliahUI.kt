package com.polije.sipeperpolije.feature.master.presentation.matakuliah.presentation.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity

data class MataKuliahUI(
    val id: Int,
    val kode: String,
    val nama: String,
    val jumlahSKS: Int,
    val namaPenampuPertama: String,
)

fun MataKuliahEntity.toUI() = MataKuliahUI(
    this.id,
    this.kode,
    this.nama,
    this.jumlahSKS,
    this.namaPengampuPertama ?: "Belum ditentukan"
)