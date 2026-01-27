package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class InsertMataKuliahUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(mataKuliah: MataKuliahEntity) =
        masterRepository.insertMataKuliah(mataKuliah)

}