package com.polije.sipeperpolije.feature.dashboard.data.repository

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.core.algoritm.Dosen
import com.polije.sipeperpolije.core.algoritm.Hari
import com.polije.sipeperpolije.core.algoritm.MataKuliah
import com.polije.sipeperpolije.core.algoritm.Ruangan
import com.polije.sipeperpolije.core.algoritm.WelchPowellAlgorithm
import com.polije.sipeperpolije.core.log
import com.polije.sipeperpolije.core.logList
import com.polije.sipeperpolije.feature.dashboard.data.model.ActivityItemModel
import com.polije.sipeperpolije.feature.dashboard.data.model.DashboardModel
import com.polije.sipeperpolije.feature.dashboard.data.model.LastGenerteSchedule
import com.polije.sipeperpolije.feature.dashboard.data.model.PreviewJadwalModel
import com.polije.sipeperpolije.feature.dashboard.data.model.PreviewJadwalModelItem
import com.polije.sipeperpolije.feature.dashboard.data.model.toEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntity
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import com.polije.sipeperpolije.feature.master.data.model.HariModel
import com.polije.sipeperpolije.feature.master.data.model.JadwalModel
import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel
import com.polije.sipeperpolije.feature.master.data.model.RuanganModel
import com.polije.sipeperpolije.feature.master.data.model.toEntity
import com.polije.sipeperpolije.feature.master.domain.entity.JadwalEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order

class DashboardRepositoryImpl(private val supabase: SupabaseClient) : DashboardRepository {

    private val welchPowellAlgorithm = WelchPowellAlgorithm()

    override suspend fun logout() {
        supabase.auth.signOut()
    }

    override suspend fun fetchDashboard(): Result<DashboardEntity> = runCatching {
        val dosenCount = supabase.from("dosen")
            .select {
                count(Count.EXACT)
                head = true
            }
            .countOrNull()?.toInt()

        val matkulCount = supabase
            .from("mata_kuliah")
            .select {
                count(Count.EXACT)
                head = true
            }.countOrNull()?.toInt()

        val jadwalCount = supabase.from("jadwal")
            .select {
                count(Count.EXACT)
                head = true
            }

        val lastGenerateJadwalStatus = supabase.from("jadwal")
            .select(Columns.list("is_success")) {
                order("created_at", Order.DESCENDING)
                limit(1)
                single()
            }
            .decodeAs<LastGenerteSchedule>()

        val recentActivities = supabase
            .from("audit_log")
            .select {
                order("changed_at", order = Order.DESCENDING)
                limit(3)
            }
            .decodeList<ActivityItemModel>()

        return Result.success(
            DashboardModel(
                dosenCount ?: 0, matkulCount ?: 0,
                isLastGeneratedScheduleSuccess = lastGenerateJadwalStatus.isSuccess ?: false,
                totalGenerateJadwalCount = jadwalCount.countOrNull()?.toInt() ?: 0,
                recentActivity = recentActivities
            ).toEntity()
        )
    }.onFailure {
        return Result.failure(it)
    }

    override suspend fun generateJadwal(
        title: String,
        semester: Semester,
        workshopTime: Int?
    ): Result<JadwalEntity> {
        val mataKuliahs = supabase
            .from("mata_kuliah_view").select {
                order("semester", Order.ASCENDING)
                order("kode", Order.ASCENDING)
                filter {
                    MataKuliahModel::isActive eq true
                }
            }.decodeList<MataKuliahModel>()
            .filter { semester.matches(it.semester) }

        val isMataKuliahValid = mataKuliahs.all { it.idPengampu != null }

        if (!isMataKuliahValid) {
            return Result.failure(Exception("Ada mata kuliah yang belum memiliki dosen pengampu"))
        }

        val dosenCache = mutableMapOf<Int, Dosen>()

        val rawJadwal = mataKuliahs.map {
            val dosen = dosenCache.getOrPut(it.idPengampu!!) {
                Dosen(it.idPengampu, it.namaPengampu.toString())
            }

            MataKuliah(
                it.nama,
                dosen,
                it.sksTeori,
                it.sksPraktek,
                semester = it.semester,
                it.namaKelas
            )
        }

        val ruangan = supabase.from("ruangan")
            .select(
                Columns.list(
                    "id", "nama",
                    "kegunaan_ruangan",
                )
            ).decodeList<RuanganModel>()
            .map { Ruangan(it.nama, it.kegunaanRuangan.toSet()) }


        val testruangan = supabase.from("ruangan")
            .select(
                Columns.list(
                    "id", "nama",
                    "kegunaan_ruangan",
                )
            ).decodeList<RuanganModel>()
        logList("ruangan", testruangan)

        val hariJam = supabase.from("hari")
            .select(
                Columns.list(
                    "id", "nama",
                    "jam_mulai",
                    "jam_selesai",
                    "jam_mulai_istirahat",
                    "jam_selesai_istirahat"
                )
            ).decodeList<HariModel>().map {
                Hari(
                    nama = it.nama,
                    jamMulai = it.jamMulai,
                    jamSelesai = it.jamSelesai,
                    jamIstirahatMulai = it.jamMulaiIstirahat,
                    jamIstirahatSelesai = it.jamSelesaiIstirahat
                )
            }

        val jadwal = welchPowellAlgorithm.buatJadwal(
            rawJadwal,
            ruangan,
            hariJam,
            overrideDurasiWorkshop = workshopTime
        )
        welchPowellAlgorithm.tampilkanJadwal(jadwal.second, daftarRuangan = ruangan)

        try {
            val result = supabase.from("jadwal")
                .insert(
                    welchPowellAlgorithm.jadwalToJson(
                        title = title,
                        jadwal.first,
                        jadwal.second,
                        " ${semester.name.uppercase()}",
                        jadwal.third
                    )
                ) {
                    select(Columns.list("id", "is_success", "title", "semester"))
                }.decodeSingle<JadwalModel>()
            return Result.success(result.toEntity())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun previewJadwal(semester: Semester): Result<List<PreviewJadwalEntity>> {
        try {
            val list = mutableListOf<PreviewJadwalModel>()
            val mataKuliah = supabase
                .from("mata_kuliah_view").select {
                    order("semester", Order.ASCENDING)
                    filter {
                        MataKuliahModel::isActive eq true
                    }
                }.decodeList<MataKuliahModel>()
                .filter { semester.matches(it.semester) }

            logList("matakuliah", mataKuliah)

            val ruangan = supabase.from("ruangan")
                .select(
                    Columns.list(
                        "id", "nama",
                        "kegunaan_ruangan",
                    )
                ).decodeList<RuanganModel>()

            val hariJam = supabase.from("hari")
                .select(
                    Columns.list(
                        "id", "nama",
                        "jam_mulai",
                        "jam_selesai",
                        "jam_mulai_istirahat",
                        "jam_selesai_istirahat"
                    )
                ).decodeList<HariModel>()


            val dosenCache = mutableMapOf<Int, Dosen>()

            val rawJadwal = mataKuliah.map {
                it.idPengampu?.let { id ->
                    dosenCache.getOrPut(id) {
                        Dosen(id, it.namaPengampu ?: "-")
                    }
                }

                MataKuliahModel(
                    kode = it.kode,
                    nama = it.nama,
                    sksTeori = it.sksTeori,
                    sksPraktek = it.sksPraktek,
                    semester = it.semester,
                    idPengampu = it.idPengampu,
                    namaPengampu = it.namaPengampu,
                    isActive = it.isActive
                )
            }

            list.add(
                PreviewJadwalModel(
                    "Mata Kuliah",
                    rawJadwal.map {
                        PreviewJadwalModelItem(
                            "${it.kode} ${it.nama}",
                            "${it.namaPengampu ?: "Belum ditentukan"} Semester ${it.semester} SKS T ${it.sksTeori} SKS P ${it.sksPraktek}"
                        )
                    })
            )


            list.add(
                PreviewJadwalModel(
                    name = "Dosen",
                    listItem = dosenCache.map { it.value }.toList()
                        .map { PreviewJadwalModelItem(it.nama, "") })
            )

            list.add(
                PreviewJadwalModel(
                    "Ruangan",
                    listItem = ruangan.map {
                        PreviewJadwalModelItem(
                            it.nama,
                            it.kegunaanRuangan?.joinToString(" & ") ?: "-"
                        )
                    })
            )

            list.add(
                PreviewJadwalModel(
                    "Hari & Jam",
                    hariJam.map {
                        PreviewJadwalModelItem(
                            it.nama,
                            "Jam Kuliah ${it.jamMulai} s/d ${it.jamSelesai} Jam Istirahat ${it.jamMulaiIstirahat ?: "-"} s/d ${it.jamSelesaiIstirahat ?: "-"}"
                        )
                    })
            )

            val result = list.map { it.toEntity() }.toList()
            return Result.success(result)
        } catch (e: Exception) {
            log(e.toString())
            return Result.failure(e)
        }
    }
}