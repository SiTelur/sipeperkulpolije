package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

data class EditRuanganState(
    val listRuangan: List<RuanganUI> = emptyList(),
    val selectedRuangan: RuanganUI? = null
)