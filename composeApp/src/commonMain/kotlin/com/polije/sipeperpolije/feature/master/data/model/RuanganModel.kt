package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RuanganModel(
    val id: Int,
    val nama: String,
    @SerialName("is_workshop")
    val isWorkshop: Boolean
)

fun RuanganModel.toEntity(): RuanganEntity = RuanganEntity(this.id, this.nama, this.isWorkshop)