package com.polije.sipeperpolije.feature.master.domain.repository

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity

interface MasterRepository {
    suspend fun getDosenPaging(offset: Int, limit: Int): Result<List<DosenEntity>>
    suspend fun getMataKuliahPaging(offset: Int, limit: Int): Result<List<MataKuliahEntity>>
}