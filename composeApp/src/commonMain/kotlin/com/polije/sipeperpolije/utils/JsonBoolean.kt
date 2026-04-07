package com.polije.sipeperpolije.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull

fun JsonElement.asBoolean(): Boolean =
    when (this) {
        is kotlinx.serialization.json.JsonPrimitive if booleanOrNull != null ->
            boolean

        else -> false
    }