package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListDosenPagingUseCase(val masterRepository: MasterRepository) {
    operator fun invoke() = masterRepository.getDosenPaging()
}


