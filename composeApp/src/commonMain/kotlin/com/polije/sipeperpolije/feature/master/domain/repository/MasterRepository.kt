package com.polije.sipeperpolije.feature.master.domain.repository

import com.polije.sipeperpolije.feature.master.data.model.TipeDosen
import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import com.polije.sipeperpolije.feature.master.domain.entity.HariEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity
import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity
import com.polije.sipeperpolije.feature.master.domain.entity.TeknisiEntity

interface MasterRepository {
    suspend fun getDosen(): Result<Map<TipeDosen, List<DosenEntity>>>
    suspend fun getDetailDosenMataKuliah(id: Int): Result<Pair<DosenEntity, List<MataKuliahEntity>>>
    suspend fun getMataKuliah(): Result<List<MataKuliahEntity>>
    suspend fun updateDosen(dosen: DosenEntity): Result<Boolean>
    suspend fun getDetailMataKuliah(id: Int): Result<MataKuliahEntity>
    suspend fun updateMataKuliah(mataKuliah: MataKuliahEntity): Result<Boolean>
    suspend fun deleteDosen(id: Int): Result<Boolean>
    suspend fun deleteMataKuliah(id: Int): Result<Boolean>
    suspend fun insertDosen(dosen: DosenEntity): Result<Boolean>
    suspend fun insertMataKuliah(mataKuliah: MataKuliahEntity): Result<MataKuliahEntity>
    suspend fun searchDosen(): Result<List<DosenEntity>>
    suspend fun getJadwal(): Result<List<JadwalEntity>>
    suspend fun getJadwalDetail(id: Int): Result<JadwalEntity>
    suspend fun getHari(): Result<List<HariEntity>>
    suspend fun updateHari(hari: HariEntity): Result<Boolean>
    suspend fun deleteHari(id: Int): Result<Boolean>
    suspend fun getRuangan(): Result<List<RuanganEntity>>
    suspend fun updateRuangan(ruangan: RuanganEntity): Result<Boolean>
    suspend fun deleteRuangan(id: Int): Result<Boolean>
    suspend fun insertRuangan(ruangan: RuanganEntity): Result<Boolean>
    suspend fun downloadJadwal(id: Int): Result<Boolean>

    suspend fun getTeknisi(): Result<List<TeknisiEntity>>
    suspend fun updateTeknisi(teknisi: TeknisiEntity): Result<Boolean>
    suspend fun deleteTeknisi(id: Int): Result<Boolean>
    suspend fun insertTeknisi(teknisi: TeknisiEntity): Result<Boolean>

}