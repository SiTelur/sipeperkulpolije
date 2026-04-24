package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel

data class EditTeknisiState(
    val listTeknisi: List<TeknisiUI> = emptyList(),
    val selectedTeknisi: TeknisiUI? = null,
    val isLoading: Boolean = false
)