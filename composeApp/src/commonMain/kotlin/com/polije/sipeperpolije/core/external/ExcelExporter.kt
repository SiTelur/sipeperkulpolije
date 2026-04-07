package com.polije.sipeperpolije.core.external

import com.polije.sipeperpolije.feature.master.data.model.JadwalModel

expect object ExcelExporter {
    fun exportJadwalExcel(
        jadwal: JadwalModel
    )
}