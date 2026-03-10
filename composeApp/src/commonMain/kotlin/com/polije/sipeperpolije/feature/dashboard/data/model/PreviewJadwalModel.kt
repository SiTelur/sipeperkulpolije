package com.polije.sipeperpolije.feature.dashboard.data.model

data class PreviewJadwalModel(val name: String, val listItem: List<PreviewJadwalModelItem>)
data class PreviewJadwalModelItem(val title: String, val desc: String)