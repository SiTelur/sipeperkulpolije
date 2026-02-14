package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel

sealed class DetailMataKuliahAction {
    data class OnDetailMataKuliahUpdate(
        val id: Int,
        val nama: String,
        val kode: String,
        val sks: Int,
        val semester: Int,
        val idDosen: Int?,
        val isWorkshop: Boolean
    ) : DetailMataKuliahAction()

    data class OnDetailMataKuliahDelete(val id: Int) : DetailMataKuliahAction()
    data class OnDetailMataKuliahLoad(val id: Int) : DetailMataKuliahAction()
}
