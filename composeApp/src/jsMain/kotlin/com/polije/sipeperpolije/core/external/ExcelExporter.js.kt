package com.polije.sipeperpolije.core.external

import com.polije.sipeperpolije.feature.master.data.model.JadwalModel

actual object ExcelExporter {
    actual fun exportJadwalExcel(jadwal: JadwalModel) {
        val workbook = XLSX.utils.book_new()

        val semuaItems = jadwal.listJadwalView.flatMap { it.items }

        val hariList = semuaItems.map { it.hari }.distinct()

        val ruanganList = semuaItems.map { it.namaRuangan }.distinct().sorted()

        val jamList = (7..17) // jam kuliah kampus


        hariList.forEach { hari ->

            val rows = mutableListOf<Array<Any?>>()

            // HEADER
            val header = mutableListOf<Any?>("Jam")
            header.addAll(ruanganList)
            rows.add(header.toTypedArray())

            // ROW JAM
            jamList.forEach { jam ->

                val row = mutableListOf<Any?>()

                row.add("$jam:00")

                ruanganList.forEach { ruangan ->

                    val jadwalItem = semuaItems.find {
                        it.hari == hari &&
                                it.namaRuangan == ruangan &&
                                jam >= it.jamMulai &&
                                jam < it.jamSelesai
                    }

                    if (jadwalItem != null) {
                        row.add(
                            "${jadwalItem.namaJadwal}\n${jadwalItem.namaDosen}"
                        )
                    } else {
                        row.add("")
                    }
                }

                rows.add(row.toTypedArray())
            }

            val sheet = XLSX.utils.aoa_to_sheet(
                rows.toTypedArray()
            )

            XLSX.utils.book_append_sheet(
                workbook,
                sheet,
                hari
            )
        }

        XLSX.writeFile(workbook, "jadwal_kuliah.xlsx")
    }
}