package com.polije.sipeperpolije.feature.master.data.repository

import com.polije.sipeperpolije.core.external.ExcelExporter
import com.polije.sipeperpolije.core.log
import com.polije.sipeperpolije.core.logList
import com.polije.sipeperpolije.feature.master.data.model.DosenModel
import com.polije.sipeperpolije.feature.master.data.model.HariModel
import com.polije.sipeperpolije.feature.master.data.model.JadwalModel
import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel
import com.polije.sipeperpolije.feature.master.data.model.RuanganModel
import com.polije.sipeperpolije.feature.master.data.model.toEntity
import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import com.polije.sipeperpolije.feature.master.domain.entity.HariEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity
import com.polije.sipeperpolije.feature.master.domain.entity.RuanganEntity
import com.polije.sipeperpolije.feature.master.domain.entity.toModel
import com.polije.sipeperpolije.feature.master.domain.repository.MasterRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class MasterRepositoryImpl(val supabase: SupabaseClient) : MasterRepository {
    override suspend fun getDosenPaging(offset: Int, limit: Int): Result<List<DosenEntity>> {
        val response = try {
            val safeOffset = offset.coerceAtLeast(0)
            val safeLimit = limit.coerceAtLeast(1)

            val data = supabase
                .from("dosen")
                .select(columns = Columns.list("id", "nama", "nidn")) {
                    range(
                        from = safeOffset.toLong(),
                        to = (safeOffset + safeLimit - 1).toLong()
                    )
                }
                .decodeList<DosenModel>().map { it.toEntity() }
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun getDetailDosenMataKuliah(id: Int): Result<List<MataKuliahEntity>> {
        val response = try {
            val data = supabase.from("mata_kuliah_view").select {
                filter {
                    MataKuliahModel::idPengampu eq id
                }
            }.decodeList<MataKuliahModel>().map { it.toEntity() }
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun getMataKuliah(): Result<List<MataKuliahEntity>> {
        val response = try {
            val data = supabase
                .from("mata_kuliah_view").select {
                    order("kode", Order.ASCENDING)
                    order("semester", Order.ASCENDING)
                }
                .decodeList<MataKuliahModel>().map { it.toEntity() }
            logList("matakuliah", data)
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun updateDosen(dosen: DosenEntity): Result<Boolean> {
        return try {
            supabase.from("dosen").update(
                {
                    set("nama", dosen.nama)
                    set("nidn", dosen.nidn)
                }
            ) {
                filter {
                    eq("id", dosen.id)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDetailMataKuliah(id: Int): Result<MataKuliahEntity> {
        val response = try {
            val data = supabase
                .from("mata_kuliah_view")
                .select {
                    filter {
                        MataKuliahModel::id eq id
                    }
                }
                .decodeSingle<MataKuliahModel>().toEntity()
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun updateMataKuliah(mataKuliah: MataKuliahEntity): Result<Boolean> {
        return try {
            supabase.from("mata_kuliah").update({
                set("kode", mataKuliah.kode)
                set("nama", mataKuliah.nama)
                set("text", mataKuliah.semester)
                set("sks_teori", mataKuliah.sksTeori)
                set("sks_praktek", mataKuliah.sksPraktek)
                set("is_active", mataKuliah.isActive)
            }) {
                filter {
                    eq("id", mataKuliah.id)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDosen(id: Int): Result<Boolean> {
        return try {
            supabase.from("dosen").delete {
                filter {
                    eq("id", id)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMataKuliah(id: Int): Result<Boolean> {
        return try {
            supabase.from("mata_kuliah").delete {
                filter {
                    eq("id", id)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertDosen(dosen: DosenEntity): Result<Boolean> {
        return try {
            supabase.from("dosen").insert(dosen.toModel())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertMataKuliah(mataKuliah: MataKuliahEntity): Result<MataKuliahEntity> {
        return try {
            val data = supabase.from("mata_kuliah").insert(mataKuliah.toModel()) {
                select(
                    Columns.list(
                        "id",
                        "kode",
                        "nama",
                        "text",
                        "sks_teori",
                        "sks_praktek",
                        "id_pengampu",
                        "is_active"
                    )
                )
            }
                .decodeSingle<MataKuliahModel>().toEntity()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchDosen(): Result<List<DosenEntity>> {
        val response = try {
            val data = supabase.from("dosen").select(Columns.list("id", "nama", "nidn")) {

            }.decodeList<DosenModel>().map { it.toEntity() }
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun getJadwal(): Result<List<JadwalEntity>> {
        val response = try {
            val data = supabase.from("jadwal")
                .select(Columns.list("id", "is_success", "title", "semester")) {
                    order("created_at", Order.DESCENDING)
                }.decodeList<JadwalModel>().map { it.toEntity() }
            logList("jadwal", data);
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            log("error ${e.message}")
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun getJadwalDetail(id: Int): Result<JadwalEntity> {
        val response = try {
            val data = supabase.from("jadwal")
                .select(
                    Columns.list(
                        "id",
                        "is_success",
                        "title",
                        "jadwal",
                        "jadwal_view",
                        "semester"
                    )
                ) {
                    filter {
                        JadwalModel::id eq id
                    }
                }.decodeSingle<JadwalModel>().toEntity()
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            log("error ${e.message}")
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun getHari(): Result<List<HariEntity>> {
        val response = try {
            val data = supabase.from("hari")
                .select(
                    Columns.list(
                        "id", "nama",
                        "jam_mulai",
                        "jam_selesai",
                        "jam_mulai_istirahat",
                        "jam_selesai_istirahat"
                    )
                ).decodeList<HariModel>().map {
                    it.toEntity()
                }
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            log("error ${e.message}")
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun updateHari(hari: HariEntity): Result<Boolean> {
        val response = try {
            supabase.from("hari").update({
                HariModel::nama setTo hari.nama
                HariModel::jamMulai setTo hari.jamMulai
                HariModel::jamSelesai setTo hari.jamSelesai
                HariModel::jamMulaiIstirahat setTo hari.jamIstirahatMulai
                HariModel::jamSelesaiIstirahat setTo hari.jamIstirahatSelesai
            }) {
                filter {
                    eq("id", hari.id)
                }

            }
            true
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun deleteHari(id: Int): Result<Boolean> {
        val response = try {
            supabase.from("hari").delete {
                filter {
                    eq("id", id)
                }
            }
            true
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }
        return Result.success(response)
    }


    override suspend fun getRuangan(): Result<List<RuanganEntity>> {
        val response = try {
            val data = supabase.from("ruangan")
                .select(
                    Columns.list(
                        "id", "nama",
                        "kegunaan_ruangan",
                    )
                ).decodeList<RuanganModel>().map {
                    it.toEntity()
                }
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            log("error ${e.message}")
            return Result.failure(e)
        }
        return Result.success(response)

    }

    override suspend fun updateRuangan(ruangan: RuanganEntity): Result<Boolean> {
        val response = try {
            supabase.from("ruangan").update({
                RuanganModel::nama setTo ruangan.nama
                RuanganModel::kegunaanRuangan setTo ruangan.tipeRuangan
            }) {
                filter {
                    eq("id", ruangan.id)
                }
            }
            true
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun deleteRuangan(id: Int): Result<Boolean> {
        val response = try {
            supabase.from("ruangan").delete {
                filter {
                    eq("id", id)
                }
            }
            true
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun insertRuangan(ruangan: RuanganEntity): Result<Boolean> {
        val response = try {
            supabase.from("ruangan").insert(ruangan.toModel())
            true
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun downloadJadwal(id: Int): Result<Boolean> {
        val response = try {
            val data = supabase.from("jadwal")
                .select(
                    Columns.list(
                        "id",
                        "is_success",
                        "title",
                        "jadwal",
                        "jadwal_view",
                        "semester"
                    )
                ) {
                    filter {
                        JadwalModel::id eq id
                    }
                }.decodeSingle<JadwalModel>()
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            log("error ${e.message}")
            return Result.failure(e)
        }

        ExcelExporter.exportJadwalExcel(jadwal = response)
        return Result.success(true)
    }

}