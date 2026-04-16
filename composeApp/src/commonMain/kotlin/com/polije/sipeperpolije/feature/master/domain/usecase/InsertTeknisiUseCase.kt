package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.TeknisiEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class InsertTeknisiUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(teknisi: TeknisiEntity): Result<Boolean> = masterRepository.insertTeknisi(teknisi)
}