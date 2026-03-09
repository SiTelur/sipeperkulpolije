package com.polije.sipeperpolije.core.external

import com.polije.sipeperpolije.feature.master.data.model.JadwalModel
import kotlin.js.json

actual object ExcelExporter {

    actual fun exportJadwalExcel(jadwal: JadwalModel) {

        val workbook = utils.book_new()
        val semuaItems = jadwal.listJadwalView.flatMap { it.items }
        val ruanganList = semuaItems.map { it.namaRuangan }.distinct().sorted()

        val hariOrder = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
        val jamSlots = (7..17).toList()

        val rows = mutableListOf<Array<Any?>>()
        val merges = mutableListOf<dynamic>()

        // Header
        val header = mutableListOf<Any?>("Hari", "Jam")
        header.addAll(ruanganList)
        rows.add(header.toTypedArray())

        var currentRow = 1

        hariOrder.forEach { hari ->

            val hariItems = semuaItems.filter { it.hari == hari }
            if (hariItems.isEmpty()) return@forEach

            val hariStartRow = currentRow

            jamSlots.forEachIndexed { idx, jam ->

                val row = mutableListOf<Any?>()

                row.add(if (idx == 0) hari else "")
                row.add(
                    "${jam.toString().padStart(2, '0')}:00 - ${
                        (jam + 1).toString().padStart(2, '0')
                    }:00"
                )

                ruanganList.forEach { ruangan ->

                    val item = hariItems.find {
                        it.namaRuangan == ruangan &&
                                jam >= it.jamMulai &&
                                jam < it.jamSelesai
                    }

                    if (item != null && jam == item.jamMulai) {

                        row.add("${item.namaJadwal}\n${item.namaDosen}\n(Smt ${item.semester})")

                        val span = item.jamSelesai - item.jamMulai

                        if (span > 1) {

                            val colIndex = ruanganList.indexOf(ruangan) + 2

                            val merge = json(
                                "s" to json(
                                    "r" to (currentRow + idx),
                                    "c" to colIndex
                                ),
                                "e" to json(
                                    "r" to (currentRow + idx + span - 1),
                                    "c" to colIndex
                                )
                            )

                            merges.add(merge)
                        }

                    } else {
                        row.add("")
                    }
                }

                rows.add(row.toTypedArray())
            }

            val hariEndRow = hariStartRow + jamSlots.size - 1

            val hariMerge = json(
                "s" to json(
                    "r" to hariStartRow,
                    "c" to 0
                ),
                "e" to json(
                    "r" to hariEndRow,
                    "c" to 0
                )
            )

            merges.add(hariMerge)

            currentRow += jamSlots.size
        }

        val sheet = utils.aoa_to_sheet(rows.toTypedArray())

        sheet["!merges"] = merges.toTypedArray()

        utils.book_append_sheet(workbook, sheet, "Jadwal")

        writeFile(workbook, "jadwal_kuliah.xlsx")
    }
}
