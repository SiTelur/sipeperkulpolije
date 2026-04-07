package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListMataKuliahUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke() =
        masterRepository.getMataKuliah()
}