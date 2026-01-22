package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import kotlinx.serialization.Serializable

@Serializable
data class DosenModel(val id: Int, val nama: String)

fun DosenModel.toEntity(): DosenEntity = DosenEntity(this.id, this.nama)