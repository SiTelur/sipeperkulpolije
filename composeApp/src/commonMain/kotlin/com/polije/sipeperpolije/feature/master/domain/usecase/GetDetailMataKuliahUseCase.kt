package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class GetDetailMataKuliahUseCase(private val repository: MasterRepository) {
    suspend operator fun invoke(id: Int) = repository.getDetailMataKuliah(id)
}