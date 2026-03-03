package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity

data class MataKuliahUI(
    val id: Int = 0,
    val kode: String,
    val nama: String,
    val semester: Int,
    val sksTeori: Int,
    val sksPraktek: Int,
    val idPengampu: Int?,
    val namaPenampu: String,
    val isActive: Boolean
)

fun MataKuliahEntity.toUI() = MataKuliahUI(
    this.id,
    this.kode,
    this.nama,
    this.semester,
    this.sksTeori,
    this.sksPraktek,
    this.idPengampu,
    this.namaPengampu ?: "Belum ditentukan",
    this.isActive
)

fun MataKuliahUI.toEntity() =
    MataKuliahEntity(
        this.id,
        this.kode,
        this.nama,
        this.semester,
        this.sksTeori,
        this.sksPraktek,
        this.idPengampu,
        this.namaPenampu,
        this.isActive,
    )
