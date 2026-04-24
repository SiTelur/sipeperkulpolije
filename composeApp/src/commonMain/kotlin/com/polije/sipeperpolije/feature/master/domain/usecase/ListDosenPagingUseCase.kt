package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListDosenPagingUseCase(val masterRepository: MasterRepository) {
    suspend operator fun invoke() =
        masterRepository.getDosen()
}


