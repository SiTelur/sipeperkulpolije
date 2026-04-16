package com.polije.sipeperpolije.feature.master.data.model

import com.polije.sipeperpolije.feature.master.domain.entity.TeknisiEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeknisiModel(
    val id: Int,
    val nama: String,
    @SerialName("is_active")
    val isActive: Boolean
)

fun TeknisiModel.toEntity() = TeknisiEntity(id, nama, isActive)