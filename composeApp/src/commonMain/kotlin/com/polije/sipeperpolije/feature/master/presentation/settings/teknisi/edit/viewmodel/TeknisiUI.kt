package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel

import com.polije.sipeperpolije.feature.master.domain.entity.TeknisiEntity

data class TeknisiUI(val id: Int, val nama: String, val isActive: Boolean)

fun TeknisiUI.toEntity() = TeknisiEntity(id, nama, isActive)

fun TeknisiEntity.toUI() = TeknisiUI(id, nama, isActive)
