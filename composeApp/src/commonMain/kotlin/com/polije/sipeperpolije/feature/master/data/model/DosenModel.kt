package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DosenModel(
    val id: Int = 0,
    val nama: String,
    val nidn: String,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("tipe_dosen") val tipeDosen: TipeDosen
)

fun DosenModel.toEntity(): DosenEntity =
    DosenEntity(this.id, this.nama, this.nidn, this.isActive, this.tipeDosen)

@Serializable
enum class TipeDosen {
    TETAP, LUAR_BIASA
}