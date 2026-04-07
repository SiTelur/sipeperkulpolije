package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

import com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel.SelectSemester

sealed class GenerateJadwalAction {
    data class OnPreviewJadwal(val semester: SelectSemester) : GenerateJadwalAction()

    data class OnGenerateJadwal(
        val title: String,
        val semester: SelectSemester,
        val overrideJamPraktikum: Int?
    ) : GenerateJadwalAction()
}