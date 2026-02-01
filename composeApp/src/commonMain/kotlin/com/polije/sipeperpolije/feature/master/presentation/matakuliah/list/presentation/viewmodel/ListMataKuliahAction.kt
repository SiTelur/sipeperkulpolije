package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

sealed class ListMataKuliahAction {
    data class OnSaveMataKuliah(
        val nama: String,
        val kode: String,
        val sks: Int,
        val semester: Int,
        val dosenID: Int?,
        val isWorkshop: Boolean
    ) : ListMataKuliahAction()

    data class OnSearcDosen(val query: String) : ListMataKuliahAction()
}