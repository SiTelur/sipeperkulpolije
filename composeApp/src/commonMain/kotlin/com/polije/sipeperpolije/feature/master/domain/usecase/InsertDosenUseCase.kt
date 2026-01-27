package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.toEntity

class InsertDosenUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(dosenUI: DosenUI) = masterRepository.insertDosen(dosenUI.toEntity())
}


