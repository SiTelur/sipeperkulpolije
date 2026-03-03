package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel

sealed class DetailMataKuliahAction {
    data class OnDetailMataKuliahUpdate(
        val id: Int,
        val nama: String,
        val kode: String,
        val sksTeori: Int,
        val sksPraktek: Int,
        val semester: Int,
        val idDosen: Int?,
        val isActive: Boolean
    ) : DetailMataKuliahAction()

    data class OnDetailMataKuliahDelete(val id: Int) : DetailMataKuliahAction()
    data class OnDetailMataKuliahLoad(val id: Int) : DetailMataKuliahAction()
}
