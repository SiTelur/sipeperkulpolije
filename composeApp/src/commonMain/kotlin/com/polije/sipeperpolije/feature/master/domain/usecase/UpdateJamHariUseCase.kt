package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.HariEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class UpdateJamHariUseCase(private val repository: MasterRepository) {
    suspend operator fun invoke(hari: HariEntity): Result<Boolean> {
        return repository.updateHari(hari)
    }
}