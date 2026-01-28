package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity

data class MataKuliahUI(
    val id: Int = 0,
    val kode: String,
    val nama: String,
    val semester: Int,
    val jumlahSKS: Int,
    val idPengampu: Int?,
    val namaPenampuPertama: String,
)

fun MataKuliahEntity.toUI() = MataKuliahUI(
    this.id,
    this.kode,
    this.nama,
    this.semester,
    this.jumlahSKS,
    this.idPengampuPertama,
    this.namaPengampuPertama ?: "Belum ditentukan"
)

fun MataKuliahUI.toEntity() =
    MataKuliahEntity(
        this.id,
        this.kode,
        this.nama,
        this.semester,
        this.jumlahSKS,
        this.idPengampu,
        null,
        idPenampuKedua = null,
        null
    )
