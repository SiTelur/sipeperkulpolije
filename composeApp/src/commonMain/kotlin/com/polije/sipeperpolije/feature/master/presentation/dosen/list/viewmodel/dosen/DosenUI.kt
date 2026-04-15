package com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen

import com.polije.sipeperpolije.feature.master.data.model.TipeDosen
import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity

data class DosenUI(
    val id: Int = 0,
    val nama: String,
    val nidn: String,
    val isActive: Boolean,
    val tipeDosen: TipeDosen
) {
    val initial =
        nama.split(" ").filter { it.isNotEmpty() }.take(2).map { it.first() }.joinToString("")
            .uppercase()
}

fun DosenEntity.toUI() =
    DosenUI(
        this.id,
        this.nama,
        nidn, isActive, tipeDosen
    )

fun DosenUI.toEntity() = DosenEntity(id, nama, nidn, isActive, tipeDosen)