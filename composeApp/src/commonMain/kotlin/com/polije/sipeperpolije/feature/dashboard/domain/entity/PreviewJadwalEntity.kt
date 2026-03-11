package com.polije.sipeperpolije.feature.dashboard.domain.entity

import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.PreviewJadwalUI
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.PreviewJadwalUIItem

data class PreviewJadwalEntity(val name: String, val listItem: List<PreviewJadwalEntityItem>)
data class PreviewJadwalEntityItem(val title: String, val desc: String)

fun PreviewJadwalEntity.toUI() = PreviewJadwalUI(name, listItem.map { it.toUI() })
fun PreviewJadwalEntityItem.toUI() = PreviewJadwalUIItem(title, desc)