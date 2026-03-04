package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MataKuliahModel(
    val id: Int = 0,
    val kode: String,
    val nama: String,
    val semester: Int,
    @SerialName("sks_teori")
    
    val sksTeori: Int,
    @SerialName("sks_praktek")
    val sksPraktek: Int,
    @SerialName("id_pengampu")
    val idPengampu: Int? = null,
    @SerialName("nama_pengampu")
    val namaPengampu: String? = null,
    @SerialName("is_active")
    val isActive: Boolean,
)

fun MataKuliahModel.toEntity() = MataKuliahEntity(
    id = id,
    kode = kode,
    nama = nama,
    semester = semester,
    sksTeori = sksTeori,
    sksPraktek = sksPraktek,
    idPengampu = idPengampu,
    isActive = isActive
)

