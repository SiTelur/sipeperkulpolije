package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.DosenModel

data class DosenEntity(val id: Int = 0, val nama: String, val nidn: String)

fun DosenEntity.toModel() = DosenModel(this.id, this.nama, this.nidn)