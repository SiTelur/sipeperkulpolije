package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class GetDetailJadwalUseCase(private val repository: MasterRepository) {
    suspend operator fun invoke(id: Int): Result<JadwalEntity> {
        return repository.getJadwalDetail(id)
    }
}
