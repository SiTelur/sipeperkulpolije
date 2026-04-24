package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.entity.TeknisiEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository

class ListTeknisiUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(): Result<List<TeknisiEntity>> = masterRepository.getTeknisi()
}