package com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.viewmodel

data class HariState(
    val isLoading: Boolean = false,
    val listOfHari: List<HariUI> = emptyList(),
    var selectedHari: HariUI? = null
)

