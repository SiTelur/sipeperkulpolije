package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel

sealed class TeknisiEvent {
    data class OnFailure(val message: String) : TeknisiEvent()
    object OnUpdateSuccess : TeknisiEvent()
    object OnDeleteSuccess : TeknisiEvent()
    object OnInsertSuccess : TeknisiEvent()
}