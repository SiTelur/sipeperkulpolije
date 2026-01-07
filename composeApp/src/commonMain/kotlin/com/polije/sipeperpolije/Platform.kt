package com.polije.sipeperpolije

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform