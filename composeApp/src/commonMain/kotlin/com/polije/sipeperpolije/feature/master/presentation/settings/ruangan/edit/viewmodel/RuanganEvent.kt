package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

sealed class RuanganEvent {
    object OnUpdateSuccess : RuanganEvent()
    data class OnUpdateFailure(val message: String) : RuanganEvent()
}