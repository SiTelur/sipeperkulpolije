package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.toEntity

class UpdateDosenUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(dosen: DosenUI) = masterRepository.updateDosen(dosen.toEntity())
}