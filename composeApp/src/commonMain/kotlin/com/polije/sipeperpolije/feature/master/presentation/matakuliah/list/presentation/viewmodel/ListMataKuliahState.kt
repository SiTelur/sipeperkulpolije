package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI

data class ListMataKuliahState(
    val mataKuliahs: List<MataKuliahUI> = emptyList(),
    val isLoading: Boolean = false,
    var result: List<DosenUI> = emptyList()
)