package com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel

import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI

sealed class DetailDosenAction {
    data class OnDosenUpdate(val dosenUI: DosenUI) : DetailDosenAction()
    data class OnDosenDelete(val id: Int) : DetailDosenAction()
}