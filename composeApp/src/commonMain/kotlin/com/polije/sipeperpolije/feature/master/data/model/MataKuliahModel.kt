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
    @SerialName("jumlah_sks")
    val jumlahSKS: Int,
    @SerialName("id_pengampu_pertama")
    val idPengampuPertama: Int? = null,
    @SerialName("nama_pengampu_pertama")
    val namaPengampuPertama: String? = null,
    @SerialName("id_pengampu_kedua")
    val idPenampuKedua: Int? = null,
    @SerialName("nama_pengampu_kedua")
    val namaPengampuKedua: String? = null
)

fun MataKuliahModel.toEntity() = MataKuliahEntity(
    id = id,
    kode = kode,
    nama = nama,
    jumlahSKS = jumlahSKS,
    idPengampuPertama = idPengampuPertama,
    namaPengampuPertama = namaPengampuPertama,
    idPenampuKedua = idPenampuKedua,
    namaPengampuKedua = namaPengampuKedua,
    semester = semester
)

