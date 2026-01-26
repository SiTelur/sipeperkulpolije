package com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel

import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI

data class DetailDosenState(
    var isLoading: Boolean = false,
    var listMataKuliah: List<MataKuliahUI> = emptyList()
)