package com.polije.sipeperpolije.feature.master.domain.repository

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity

interface MasterRepository {
    suspend fun getDosenPaging(offset: Int, limit: Int): Result<List<DosenEntity>>
}