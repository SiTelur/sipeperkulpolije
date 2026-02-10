package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListJadwalUseCase(private val repository: MasterRepository) {
    suspend operator fun invoke(): Result<List<JadwalEntity>> = repository.getJadwal()
}