package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

sealed class RuanganAction {
    data class OnRuanganSelected(val ruangan: RuanganUI) : RuanganAction()
    object OnDismissRuangan : RuanganAction()
    data class OnUpdateRuangan(
        val ruangan: RuanganUI
    ) : RuanganAction()


}