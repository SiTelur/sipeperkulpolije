package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class DeleteTeknisiUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(id: Int): Result<Boolean> = masterRepository.deleteTeknisi(id)
}