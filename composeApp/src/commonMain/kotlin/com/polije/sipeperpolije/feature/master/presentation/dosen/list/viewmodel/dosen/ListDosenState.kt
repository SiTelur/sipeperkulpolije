package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import com.polije.sipeperpolije.feature.master.data.model.TipeDosen

data class ListDosenState(
    val dosenGrouped: List<DosenGrouped> = emptyList(),
    val isLoadingMore: Boolean = false,
    val hasError: String? = null
)

data class DosenGrouped(val tipeDosen: TipeDosen, val listDosen: List<DosenUI>)