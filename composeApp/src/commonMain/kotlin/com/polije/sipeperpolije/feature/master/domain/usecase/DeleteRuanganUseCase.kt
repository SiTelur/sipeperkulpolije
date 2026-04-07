package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class DeleteRuanganUseCase(private val repository: MasterRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteRuangan(id)
}