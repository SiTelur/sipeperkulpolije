package com.polije.sipeperpolije.feature.master.domain.entity

import com.polije.sipeperpolije.core.algoritm.TipePenggunaan
import com.polije.sipeperpolije.feature.master.data.model.RuanganModel
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.RuanganUI
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.TipeRuangan

data class RuanganEntity(
    val id: Int,
    val nama: String,
    val tipeRuangan: List<TipePenggunaan> = emptyList()
)

fun RuanganEntity.toModel(): RuanganModel = RuanganModel(id, nama, tipeRuangan)

fun RuanganEntity.toUI() =
    RuanganUI(id, nama, TipeRuangan.entries.first { it.value == tipeRuangan })