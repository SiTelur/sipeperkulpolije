package com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel

import com.polije.sipeperpolije.feature.master.data.model.TipeDosen
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI

data class DetailDosenState(
    var isLoading: Boolean = false,
    val dosenDetail: DosenUI = DosenUI(0, "", "",false, TipeDosen.TETAP),
    var listMataKuliah: List<MataKuliahUI> = emptyList()
)