package com.polije.sipeperpolije.feature.master.presentation.matakuliah.presentation.viewmodel

data class ListMataKuliahState(
    val mataKuliahs: List<MataKuliahUI> = emptyList(),
    val isLoadingMore: Boolean = false,
    val hasError: String? = null
)