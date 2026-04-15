package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.DosenModel
import com.polije.sipeperpolije.feature.master.data.model.TipeDosen

data class DosenEntity(
    val id: Int = 0,
    val nama: String,
    val nidn: String,
    val isActive: Boolean,
    val tipeDosen: TipeDosen
)

fun DosenEntity.toModel() = DosenModel(this.id, this.nama, this.nidn, this.isActive, this.tipeDosen)