package com.polije.sipeperpolije.feature.master.presentation.dosen.viewmodel.dosen

data class ListDosenState(
    val dosens: List<DosenUI> = emptyList(),
    val isLoadingMore: Boolean = false,
    val hasError: String? = null
)