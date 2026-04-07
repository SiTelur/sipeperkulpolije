package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListRuanganUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(): Result<List<RuanganEntity>> = masterRepository.getRuangan()
}