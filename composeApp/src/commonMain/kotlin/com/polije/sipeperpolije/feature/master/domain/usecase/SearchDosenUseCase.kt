package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class SearchDosenUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke() = masterRepository.searchDosen()
}