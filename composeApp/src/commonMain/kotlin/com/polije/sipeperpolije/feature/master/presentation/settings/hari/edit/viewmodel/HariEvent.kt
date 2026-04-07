package com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.viewmodel

sealed class HariEvent {
    object OnUpdateSuccess : HariEvent()
    data class OnUpdateFailure(val message: String) : HariEvent()
}