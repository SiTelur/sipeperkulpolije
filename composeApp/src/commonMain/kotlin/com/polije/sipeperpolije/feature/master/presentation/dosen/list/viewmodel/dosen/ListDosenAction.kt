package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import com.polije.sipeperpolije.feature.master.data.model.TipeDosen

sealed class ListDosenAction {
    data class InsertDosen(
        val nama: String,
        val nidn: String,
        val isActive: Boolean,
        val tipeDosen: TipeDosen
    ) : ListDosenAction()

    object OnInitial : ListDosenAction()
}