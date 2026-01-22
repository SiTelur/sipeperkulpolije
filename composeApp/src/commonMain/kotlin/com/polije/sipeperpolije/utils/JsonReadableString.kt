package com.polije.sipeperpolije.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

fun JsonElement.asReadableString(): String =
    when {
        this is kotlinx.serialization.json.JsonPrimitive && isString ->
            content

        this is kotlinx.serialization.json.JsonPrimitive && booleanOrNull != null ->
            boolean.toString()

        this is kotlinx.serialization.json.JsonPrimitive && intOrNull != null ->
            int.toString()

        this is kotlinx.serialization.json.JsonPrimitive && doubleOrNull != null ->
            double.toString()

        else -> toString()
    }
