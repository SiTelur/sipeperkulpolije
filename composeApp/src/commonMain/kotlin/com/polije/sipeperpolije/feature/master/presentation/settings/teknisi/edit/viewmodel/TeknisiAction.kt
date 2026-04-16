package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel

sealed class TeknisiAction {
    data class OnTeknisiSelected(val teknisi: TeknisiUI) : TeknisiAction()
    object OnDismissTeknisi : TeknisiAction()
    data class OnUpdateTeknisi(val teknisi: TeknisiUI) : TeknisiAction()
    data class OnInsertTeknisi(val teknisi: TeknisiUI) : TeknisiAction()
    data class OnDeleteTeknisi(val id: Int) : TeknisiAction()
}