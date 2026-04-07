package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.core.algoritm.TipePenggunaan
import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RuanganModel(
    val id: Int,
    val nama: String,
    @SerialName("kegunaan_ruangan")
    val kegunaanRuangan: List<TipePenggunaan>
)

fun RuanganModel.toEntity(): RuanganEntity =
    RuanganEntity(this.id, this.nama, this.kegunaanRuangan)