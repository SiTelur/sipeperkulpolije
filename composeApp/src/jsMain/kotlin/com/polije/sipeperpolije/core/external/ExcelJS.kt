package com.polije.sipeperpolije.core.external

import kotlin.js.Json

// Bukan object, tapi imported sebagai named exports
external val utils: XLSXUtils
external val version: String

external fun writeFile(workbook: dynamic, fileName: String)
external fun writeFile(workbook: dynamic, fileName: String, options: dynamic)
external fun write(workbook: dynamic, options: dynamic): dynamic
external fun read(data: dynamic, options: dynamic = definedExternally): dynamic
external fun readFile(fileName: String, options: dynamic = definedExternally): dynamic

external interface XLSXUtils {
    fun aoa_to_sheet(data: Array<Array<Any?>>): dynamic
    fun aoa_to_sheet(data: Array<Array<Any?>>, options: dynamic): dynamic

    fun json_to_sheet(data: Array<Json>): dynamic
    fun json_to_sheet(data: Array<Json>, options: dynamic): dynamic

    fun sheet_to_json(worksheet: dynamic): Array<Json>
    fun sheet_to_json(worksheet: dynamic, options: dynamic): Array<Json>

    fun sheet_to_csv(worksheet: dynamic): String

    fun book_new(): dynamic
    fun book_append_sheet(workbook: dynamic, worksheet: dynamic, name: String)

    fun encode_cell(cell: dynamic): String
    fun decode_range(range: String): dynamic
    fun encode_range(range: dynamic): String
}