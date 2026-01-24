package com.polije.sipeperpolije.feature.master.domain.entity

data class MataKuliahEntity(
    val id: Int,
    val kode: String,
    val nama: String,
    val semester: Int,
    val jumlahSKS: Int,
    val idPengampuPertama: Int?,
    val namaPengampuPertama: String?,
    val idPenampuKedua: Int?,
    val namaPengampuKedua: String?
)