package com.polije.sipeperpolije.core.external

import com.polije.sipeperpolije.feature.master.data.model.JadwalModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

actual object ExcelExporter {

    private fun getSemesterBgColor(semester: Int): String = when (semester) {
        1, 2 -> "FCE883"  // Yellow
        3, 4 -> "90EE90"  // Green
        5, 6 -> "ADD8E6"  // Blue
        else -> "AAAAAA"  // Gray
    }

    private fun getSemesterFontColor(semester: Int): String = "000000"

    private fun headerStyle(): dynamic {
        val style = js("({})")
        style.fill = js("({})")
        style.fill.type = "pattern"
        style.fill.pattern = "solid"
        style.fill.fgColor = js("({})")
        style.fill.fgColor.argb = "FF1E3A5F"
        style.font = js("({})")
        style.font.bold = true
        style.font.size = 11
        style.font.color = js("({})")
        style.font.color.argb = "FFFFFFFF"
        style.alignment = js("({})")
        style.alignment.horizontal = "center"
        style.alignment.vertical = "middle"
        style.alignment.wrapText = true
        style.border = thinBorder("FFCCCCCC")
        return style
    }

    private fun hariStyle(): dynamic {
        val style = js("({})")
        style.fill = js("({})")
        style.fill.type = "pattern"
        style.fill.pattern = "solid"
        style.fill.fgColor = js("({})")
        style.fill.fgColor.argb = "FF2D4E7A"
        style.font = js("({})")
        style.font.bold = true
        style.font.size = 11
        style.font.color = js("({})")
        style.font.color.argb = "FFFFFFFF"
        style.alignment = js("({})")
        style.alignment.horizontal = "center"
        style.alignment.vertical = "middle"
        style.border = thinBorder("FFCCCCCC")
        return style
    }

    private fun jamStyle(): dynamic {
        val style = js("({})")
        style.fill = js("({})")
        style.fill.type = "pattern"
        style.fill.pattern = "solid"
        style.fill.fgColor = js("({})")
        style.fill.fgColor.argb = "FFF0F4F8"
        style.font = js("({})")
        style.font.size = 10
        style.font.color = js("({})")
        style.font.color.argb = "FF333333"
        style.alignment = js("({})")
        style.alignment.horizontal = "center"
        style.alignment.vertical = "middle"
        style.border = thinBorder("FFCCCCCC")
        return style
    }

    private fun jadwalStyle(semester: Int): dynamic {
        val bgColor = "FF${getSemesterBgColor(semester)}"
        val fontColor = "FF${getSemesterFontColor(semester)}"
        val style = js("({})")
        style.fill = js("({})")
        style.fill.type = "pattern"
        style.fill.pattern = "solid"
        style.fill.fgColor = js("({})")
        style.fill.fgColor.argb = bgColor
        style.font = js("({})")
        style.font.bold = true
        style.font.size = 10
        style.font.color = js("({})")
        style.font.color.argb = fontColor
        style.alignment = js("({})")
        style.alignment.horizontal = "center"
        style.alignment.vertical = "middle"
        style.alignment.wrapText = true
        style.border = thinBorder("FFCCCCCC")
        return style
    }

    private fun emptyStyle(): dynamic {
        val style = js("({})")
        style.fill = js("({})")
        style.fill.type = "pattern"
        style.fill.pattern = "solid"
        style.fill.fgColor = js("({})")
        style.fill.fgColor.argb = "FFFAFAFA"
        style.border = thinBorder("FFEEEEEE")
        return style
    }

    private fun thinBorder(argb: String): dynamic {
        val side = {
            val s = js("({})")
            s.style = "thin"
            s.color = js("({})")
            s.color.argb = argb
            s
        }
        val border = js("({})")
        border.top = side()
        border.bottom = side()
        border.left = side()
        border.right = side()
        return border
    }

    actual fun exportJadwalExcel(jadwal: JadwalModel) {

        val workbook = js("new (require('exceljs').Workbook)()")
        val worksheet = workbook.addWorksheet("Jadwal")

        val semuaItems = jadwal.listJadwalView.flatMap { it.items }
        val ruanganList = semuaItems.map { it.namaRuangan }.distinct().sorted()
        val hariOrder = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
        val jamSlots = (7..17).toList()

        // ── Columns ───────────────────────────────────────────────────────────
        val columns = js("[]")
        js("columns.push({header: 'Hari', key: 'hari', width: 12})")
        js("columns.push({header: 'Jam',  key: 'jam',  width: 20})")
        ruanganList.forEach { ruangan ->
            val col = js("({})")
            col.header = ruangan
            col.key = ruangan
            col.width = 30
            js("columns.push(col)")
        }
        worksheet.columns = columns

        // ── Header row styling ────────────────────────────────────────────────
        val headerRow = worksheet.getRow(1)
        headerRow.height = 24
        for (i in 1..(ruanganList.size + 2)) {
            val cell = headerRow.getCell(i)
            val s = headerStyle()
            cell.fill = s.fill
            cell.font = s.font
            cell.alignment = s.alignment
            cell.border = s.border
        }
        headerRow.commit()

        // ── Data rows ─────────────────────────────────────────────────────────
        var currentExcelRow = 2  // row 1 = header

        hariOrder.forEach { hari ->

            val hariItems = semuaItems.filter { it.hari == hari }
            if (hariItems.isEmpty()) return@forEach

            val hariStartExcelRow = currentExcelRow

            jamSlots.forEachIndexed { idx, jam ->

                val excelRowNum = currentExcelRow + idx
                val row = worksheet.getRow(excelRowNum)
                row.height = 42

                // Kolom Hari
                val hariCell = row.getCell(1)
                hariCell.value = if (idx == 0) hari else ""
                val hs = hariStyle()
                hariCell.fill = hs.fill
                hariCell.font = hs.font
                hariCell.alignment = hs.alignment
                hariCell.border = hs.border

                // Kolom Jam
                val jamCell = row.getCell(2)
                jamCell.value = "${jam.toString().padStart(2, '0')}:00 - ${
                    (jam + 1).toString().padStart(2, '0')
                }:00"
                val js2 = jamStyle()
                jamCell.fill = js2.fill
                jamCell.font = js2.font
                jamCell.alignment = js2.alignment
                jamCell.border = js2.border

                // Kolom Ruangan
                ruanganList.forEachIndexed { rIdx, ruangan ->
                    val colNum = rIdx + 3
                    val item = hariItems.find {
                        it.namaRuangan == ruangan &&
                                jam >= it.jamMulai &&
                                jam < it.jamSelesai
                    }
                    val ruanganCell = row.getCell(colNum)

                    if (item != null && jam == item.jamMulai) {
                        ruanganCell.value = buildString {
                            append(item.namaJadwal)
                            append("\n")
                            append(item.namaDosen)
                            if (!item.namaTeknisi.isNullOrEmpty()) {
                                append("\n(Teknisi: ${item.namaTeknisi})")
                            }
                            append("\n(Smt ${item.semester})")
                        }

                        val js3 = jadwalStyle(item.semester)
                        ruanganCell.fill = js3.fill
                        ruanganCell.font = js3.font
                        ruanganCell.alignment = js3.alignment
                        ruanganCell.border = js3.border

                        // Merge jika span > 1
                        val span = item.jamSelesai - item.jamMulai
                        if (span > 1) {
                            worksheet.mergeCells(
                                excelRowNum, colNum,
                                excelRowNum + span - 1, colNum
                            )
                        }

                    } else if (item == null) {
                        val es = emptyStyle()
                        ruanganCell.fill = es.fill
                        ruanganCell.border = es.border
                    }
                }

                row.commit()
            }

            // Merge kolom Hari
            worksheet.mergeCells(
                hariStartExcelRow, 1,
                hariStartExcelRow + jamSlots.size - 1, 1
            )

            currentExcelRow += jamSlots.size
        }

        // ── Export via Blob ───────────────────────────────────────────────────
        // Ganti dengan
        MainScope().launch {
            val buffer =
                workbook.xlsx.writeBuffer().unsafeCast<kotlin.js.Promise<dynamic>>().await()
            val uint8 = js("new Uint8Array(buffer)")
            val blob =
                js("new Blob([uint8], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })")
            val url = js("URL.createObjectURL(blob)")
            val a = js("document.createElement('a')")
            a.href = url
            a.download = "jadwal_kuliah.xlsx"
            js("document.body.appendChild(a)")
            a.click()
            js("URL.revokeObjectURL(url)")
            js("document.body.removeChild(a)")
        }
    }
}