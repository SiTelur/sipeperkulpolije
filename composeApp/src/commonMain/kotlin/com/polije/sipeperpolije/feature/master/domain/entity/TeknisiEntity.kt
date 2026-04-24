package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.feature.master.data.model.TeknisiModel

data class TeknisiEntity(val id: Int, val nama: String, val isActive: Boolean)

fun TeknisiEntity.toModel() = TeknisiModel(id, nama, isActive)
