package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

data class GenerateJadwalState(
    val isLoading: Boolean = false,
    val list: List<PreviewJadwalUI> = emptyList()
)
