package com.polije.sipeperpolije.feature.master.domain.repository

import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity

interface MasterRepository {
    suspend fun getDosenPaging(offset: Int, limit: Int): Result<List<DosenEntity>>
    suspend fun getDetailDosenMataKuliah(id: Int): Result<List<MataKuliahEntity>>
    suspend fun getMataKuliahPaging(offset: Int, limit: Int): Result<List<MataKuliahEntity>>
    suspend fun updateDosen(dosen: DosenEntity): Result<Boolean>
    suspend fun updateMataKuliah(mataKuliah: MataKuliahEntity): Result<Boolean>
    suspend fun deleteDosen(id: Int): Result<Boolean>
    suspend fun deleteMataKuliah(id: Int): Result<Boolean>

}