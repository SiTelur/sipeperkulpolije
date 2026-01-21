package com.polije.sipeperpolije.feature.master.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.polije.sipeperpolije.feature.master.data.datasource.DosenPagingDataSource
import com.polije.sipeperpolije.feature.master.data.model.toEntity
import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class MasterRepositoryImpl(val supabase: SupabaseClient) : MasterRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDosenPaging(): Flow<PagingData<DosenEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
            pagingSourceFactory = { DosenPagingDataSource(supabase) }
        ).flow.mapLatest { value -> value.map { dosenModel -> dosenModel.toEntity() } }
    }
}