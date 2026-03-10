package com.polije.sipeperpolije.feature.dashboard.domain.entity

data class PreviewJadwalEntity(val name: String, val listItem: List<PreviewJadwalEntityItem>)
data class PreviewJadwalEntityItem(val title: String, val desc: String)