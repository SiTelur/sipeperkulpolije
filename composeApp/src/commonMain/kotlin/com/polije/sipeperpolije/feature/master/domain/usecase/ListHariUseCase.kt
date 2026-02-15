package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListHariUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke() = masterRepository.getHari()
}