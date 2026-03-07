package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

sealed class RuanganEvent {
    object OnUpdateSuccess : RuanganEvent()
    data class OnFailure(val message: String) : RuanganEvent()
    object OnDeleteSuccess : RuanganEvent()
    object OnInsertSuccess : RuanganEvent()

}