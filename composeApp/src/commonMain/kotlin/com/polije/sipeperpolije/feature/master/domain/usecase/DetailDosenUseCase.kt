package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.toUI

class DetailDosenUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(id: Int) =
        masterRepository.getDetailDosenMataKuliah(id).map { result -> result.map { it.toUI() } }
}