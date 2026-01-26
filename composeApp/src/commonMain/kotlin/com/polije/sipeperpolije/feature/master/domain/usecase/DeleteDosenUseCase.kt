package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class DeleteDosenUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(id: Int) = masterRepository.deleteDosen(id)
}