package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel

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

fun MataKuliahEntity.toModel() = MataKuliahModel(
    id = id,
    kode = kode,
    nama = nama,
    semester = semester, jumlahSKS = jumlahSKS, idPengampuPertama = idPengampuPertama
)