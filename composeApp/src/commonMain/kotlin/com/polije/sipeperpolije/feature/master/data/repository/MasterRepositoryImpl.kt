package com.polije.sipeperpolije.feature.master.data.repository

import com.polije.sipeperpolije.core.log
import com.polije.sipeperpolije.core.logList
import com.polije.sipeperpolije.feature.master.data.model.DosenModel
import com.polije.sipeperpolije.feature.master.data.model.JadwalModel
import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel
import com.polije.sipeperpolije.feature.master.data.model.toEntity
import com.polije.sipeperpolije.feature.master.domain.entity.DosenEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import com.polije.sipeperpolije.feature.master.domain.entity.MataKuliahEntity
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
                    MataKuliahModel::idPengampuPertama eq id
                }
            }.decodeList<MataKuliahModel>().map { it.toEntity() }
            data
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive();
            return Result.failure(e)
        }
        return Result.success(response)
    }

    override suspend fun getMataKuliahPaging(
        offset: Int,
        limit: Int
    ): Result<List<MataKuliahEntity>> {
        val response = try {
            val safeOffset = offset.coerceAtLeast(0)
            val safeLimit = limit.coerceAtLeast(1)

            val data = supabase
                .from("mata_kuliah_view")
                .select {
                    range(
                        from = safeOffset.toLong(),
                        to = (safeOffset + safeLimit - 1).toLong()
                    )
                    order("nama", Order.ASCENDING)
                }
                .decodeList<MataKuliahModel>().map { it.toEntity() }
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

    override suspend fun updateMataKuliah(mataKuliah: MataKuliahEntity): Result<Boolean> {
        return try {
            supabase.from("mata_kuliah").update({
                set("kode", mataKuliah.kode)
                set("nama", mataKuliah.nama)
                set("semester", mataKuliah.semester)
                set("jumlah_sks", mataKuliah.jumlahSKS)
                set("id_pengampu_pertama", mataKuliah.idPengampuPertama)
                set("is_workshop", mataKuliah.isWorkshop)
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
                        "semester",
                        "jumlah_sks",
                        "id_pengampu_pertama",
                        "is_workshop"
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
                .select(Columns.list("id", "is_success", "jadwal", "semester")) {

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


}