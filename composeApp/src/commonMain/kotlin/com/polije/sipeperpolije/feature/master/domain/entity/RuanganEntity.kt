package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.RuanganModel

data class RuanganEntity(val id: Int, val nama: String, val isWorkshop: Boolean)

fun RuanganEntity.toModel(): RuanganModel = RuanganModel(id, nama, isWorkshop)