package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel

sealed class ListMataKuliahAction {
    data class OnSaveMataKuliah(
        val nama: String,
        val kode: String,
        val sksTeori: Int,
        val sksPraktek: Int,
        val semester: Int,
        val dosenID: Int?,
        val namaDosen: String?,
        val isActive: Boolean
    ) : ListMataKuliahAction()

    data class OnSearcDosen(val query: String) : ListMataKuliahAction()

    data class OnChangeStatusChip(val status: Boolean?) : ListMataKuliahAction()
}