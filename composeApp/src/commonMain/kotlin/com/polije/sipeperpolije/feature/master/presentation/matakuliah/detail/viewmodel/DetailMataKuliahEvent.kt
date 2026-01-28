package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel

sealed class DetailMataKuliahEvent {
    object OnSuccessUpdateMataKuliah : DetailMataKuliahEvent()
    data class OnFailureUpdateMataKuliah(val message: String) : DetailMataKuliahEvent()
    object OnSuccessDeleteMataKuliah : DetailMataKuliahEvent()
    data class OnFailureDeleteMataKuliah(val message: String) : DetailMataKuliahEvent()

}