@file:JsModule("xlsx")
@file:JsNonModule

package com.polije.sipeperpolije.core.external

import kotlin.js.Json

external object XLSX {
    val utils: XLSXUtils

    fun writeFile(workbook: dynamic, fileName: String)
    fun writeFile(workbook: dynamic, fileName: String, options: dynamic)

    // kalau ingin generate buffer/blob manual
    fun write(workbook: dynamic, options: dynamic): dynamic
}

external interface XLSXUtils {

    // Array of Arrays → worksheet
    fun aoa_to_sheet(data: Array<Array<Any?>>): dynamic
    fun aoa_to_sheet(data: Array<Array<Any?>>, options: dynamic): dynamic

    // JSON array → worksheet
    fun json_to_sheet(data: Array<Json>): dynamic
    fun json_to_sheet(data: Array<Json>, options: dynamic): dynamic

    // Workbook
    fun book_new(): dynamic
    fun book_append_sheet(workbook: dynamic, worksheet: dynamic, name: String)

    // helper
    fun encode_cell(cell: dynamic): String
    fun decode_range(range: String): dynamic
}