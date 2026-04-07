package com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.viewmodel

sealed class HariAction {
    data class OnHariSelected(val hari: HariUI) : HariAction()
    object OnDismissHari : HariAction()
    data class OnUpdateHari(
        val hari: HariUI
    ) : HariAction()

}