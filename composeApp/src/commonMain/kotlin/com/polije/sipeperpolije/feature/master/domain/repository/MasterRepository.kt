package com.polije.sipeperpolije.feature.master.domain.repository

import androidx.paging.PagingData
import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import kotlinx.coroutines.flow.Flow

interface MasterRepository {
    fun getDosenPaging(): Flow<PagingData<DosenEntity>>
}