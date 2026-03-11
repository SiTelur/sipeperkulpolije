package com.polije.sipeperpolije.core

import kotlin.js.JsName

@JsName("logging")
fun log(message: String) {
    console.log(message)
}

@JsName("logList")
fun logList(label: String, list: List<Any?>) {
//    console.log(label, list.toTypedArray())
}
