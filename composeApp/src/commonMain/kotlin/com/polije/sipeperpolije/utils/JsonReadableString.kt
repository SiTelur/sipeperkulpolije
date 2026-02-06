package com.polije.sipeperpolije.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

fun JsonElement.asReadableString(): String =
    when (this) {
        is kotlinx.serialization.json.JsonPrimitive if isString ->
            content

        is kotlinx.serialization.json.JsonPrimitive if booleanOrNull != null ->
            boolean.toString()

        is kotlinx.serialization.json.JsonPrimitive if intOrNull != null ->
            int.toString()

        is kotlinx.serialization.json.JsonPrimitive if doubleOrNull != null ->
            double.toString()

        else -> toString()
    }
