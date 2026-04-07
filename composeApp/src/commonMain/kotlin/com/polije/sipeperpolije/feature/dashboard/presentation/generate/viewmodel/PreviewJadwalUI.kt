package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

data class PreviewJadwalUI(val nama: String, val list: List<PreviewJadwalUIItem>)

data class PreviewJadwalUIItem(val name: String, val desc: String)