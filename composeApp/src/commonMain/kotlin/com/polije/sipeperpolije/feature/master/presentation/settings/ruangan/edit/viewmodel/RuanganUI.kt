package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel

import com.polije.sipeperpolije.core.algoritm.TipePenggunaan
import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity

data class RuanganUI(val id: Int, val nama: String, val tipeRuangan: TipeRuangan)

enum class TipeRuangan(val label: String, val value: List<TipePenggunaan>) {
    TEORI("Teori", listOf(TipePenggunaan.TEORI)),
    PRAKTEK("Praktek", listOf(TipePenggunaan.PRAKTIK)),
    TEORIPRAKTEK("Teori & Praktek", listOf(TipePenggunaan.TEORI, TipePenggunaan.PRAKTIK))
}

fun RuanganUI.toEntity() = RuanganEntity(id, nama, tipeRuangan.value)