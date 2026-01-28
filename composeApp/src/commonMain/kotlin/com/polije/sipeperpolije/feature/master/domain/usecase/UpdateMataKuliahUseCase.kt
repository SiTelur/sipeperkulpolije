package com.polije.sipeperpolije.feature.master.domain.usecase

import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.toEntity

class UpdateMataKuliahUseCase(private val masterRepository: MasterRepository) {
    suspend operator fun invoke(mataKuliah: MataKuliahUI) =
        masterRepository.updateMataKuliah(mataKuliah.toEntity())


}