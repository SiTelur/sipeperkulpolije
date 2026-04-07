package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class UpdateRuanganUseCase(private val repository: MasterRepository) {
    suspend operator fun invoke(ruangan: RuanganEntity) = repository.updateRuangan(ruangan)
}