package com.polije.sipeperpolije.core

enum class Semester {
    GANJIL {
        override fun matches(n: Int) = n % 2 != 0
    },
    GENAP {
        override fun matches(n: Int) = n % 2 == 0
    };

    abstract fun matches(n: Int): Boolean
}

