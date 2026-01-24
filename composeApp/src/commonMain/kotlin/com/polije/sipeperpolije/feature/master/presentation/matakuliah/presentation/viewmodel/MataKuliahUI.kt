package com.polije.sipeperpolije.feature.master.presentation.matakuliah.presentation.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity
import kotlinx.serialization.Serializable

@Serializable
data class MataKuliahUI(
    val id: Int,
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
    this.jumlahSKS,
    this.semester,
    this.idPengampuPertama,
    this.namaPengampuPertama ?: "Belum ditentukan"
)