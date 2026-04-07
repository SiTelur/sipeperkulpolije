package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

sealed class ListDosenAction {
    data class InsertDosen(val nama: String, val nidn: String) : ListDosenAction()
}