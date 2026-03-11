package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

import com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel.SelectSemester

sealed class GenerateJadwalAction {
    data class OnGenerateJadwal(val semester: SelectSemester) : GenerateJadwalAction()

}