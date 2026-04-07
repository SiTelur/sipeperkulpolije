package com.polije.sipeperpolije.feature.dashboard.data.model

import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntityItem

data class PreviewJadwalModel(val name: String, val listItem: List<PreviewJadwalModelItem>)
data class PreviewJadwalModelItem(val title: String, val desc: String)

fun PreviewJadwalModelItem.toEntity() = PreviewJadwalEntityItem(this.title, this.desc)
fun PreviewJadwalModel.toEntity() =
    PreviewJadwalEntity(this.name, this.listItem.map { it.toEntity() })