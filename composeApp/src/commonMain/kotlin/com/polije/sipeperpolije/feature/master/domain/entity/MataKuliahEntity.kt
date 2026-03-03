package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel

data class MataKuliahEntity(
    val id: Int,
    val kode: String,
    val nama: String,
    val semester: Int,
    val sksTeori: Int, val sksPraktek: Int, val idPengampu: Int? = null,
    val namaPengampu: String? = null,
    val isActive: Boolean
)

fun MataKuliahEntity.toModel() = MataKuliahModel(
    id = id,
    kode = kode,
    nama = nama,
    semester = semester,
    sksTeori = sksTeori,
    sksPraktek = sksPraktek,
    idPengampu = idPengampu,
    namaPengampu = namaPengampu,
    isActive = isActive
)