package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel

import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI

data class DetailMataKuliahState(
    val result: List<DosenUI> = emptyList(),
    val detail: MataKuliahUI?, val isLoading: Boolean = false
)