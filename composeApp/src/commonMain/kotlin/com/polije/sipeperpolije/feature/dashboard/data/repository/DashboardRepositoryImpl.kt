package com.polije.sipeperpolije.feature.dashboard.data.repository

import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.core.algoritm.Dosen
import com.polije.sipeperpolije.core.algoritm.Hari
import com.polije.sipeperpolije.core.algoritm.MataKuliah
import com.polije.sipeperpolije.core.algoritm.Ruangan
import com.polije.sipeperpolije.core.algoritm.TipePenggunaan
import com.polije.sipeperpolije.core.algoritm.WelchPowellAlgorithm
import com.polije.sipeperpolije.core.log
import com.polije.sipeperpolije.core.logList
import com.polije.sipeperpolije.feature.dashboard.data.model.ActivityItemModel
import com.polije.sipeperpolije.feature.dashboard.data.model.DashboardModel
import com.polije.sipeperpolije.feature.dashboard.data.model.PreviewJadwalModel
import com.polije.sipeperpolije.feature.dashboard.data.model.PreviewJadwalModelItem
import com.polije.sipeperpolije.feature.dashboard.data.model.toEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.PreviewJadwalEntity
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import com.polije.sipeperpolije.feature.master.data.model.HariModel
import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel
import com.polije.sipeperpolije.feature.master.data.model.RuanganModel
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
                isLastGeneratedScheduleSuccess = false,
                recentActivity = recentActivities
            ).toEntity()
        )
    }.onFailure {
        return Result.failure(it)
    }

    override suspend fun generateJadwal(
        semester: Semester,
        workshopTime: Int?
    ): Result<Boolean> {
        val responses = supabase
            .from("mata_kuliah_view").select {
                order("nama", Order.ASCENDING)
            }.decodeList<MataKuliahModel>()
            .filter { semester.matches(it.semester) }

        log("$responses")

        logList("mataKuliah", responses)

        val isMataKuliahValid = responses.all { it.idPengampu != null }

        if (!isMataKuliahValid) {
            return Result.failure(Exception("Ada mata kuliah yang belum memiliki dosen pengampu"))
        }

        val dosenCache = mutableMapOf<Int, Dosen>()

        val rawJadwal = responses.map {
            val dosen = dosenCache.getOrPut(it.idPengampu!!) {
                Dosen(it.idPengampu, it.namaPengampu.toString())
            }

            MataKuliah(
                it.nama,
                dosen,
                it.sksTeori,
                it.sksPraktek,
                semester = it.semester,
            )

        }

//        val daftarMataKuliah = listOf(
//            MataKuliah("Pancasila", asmunir, sks = 2),
//            MataKuliah("Logika dan Algoritma", akas, sks = 2),
//            MataKuliah("WSIBD A", rani, sks = 4, isWorkshop = true),
//            MataKuliah("WSIBD B", sholihah, sks = 4, isWorkshop = true),
//            MataKuliah("WSIBD C", rifqi, sks = 4, isWorkshop = true),
//            MataKuliah("WBD A", akas, sks = 4, isWorkshop = true),
//            MataKuliah("WBD B", dhonny, sks = 4, isWorkshop = true),
//            MataKuliah("WBD C", dhonny, sks = 4, isWorkshop = true),
//            MataKuliah("Interaksi Manusia Komputer", dhonny, sks = 2),
//            MataKuliah("Pemrograman Dasar", sholihah, sks = 2),
//            MataKuliah("Agama", aris, 2),
//            MataKuliah("Inggris", iin, sks = 4),
//            MataKuliah("WKPL A", rani, 4, isWorkshop = true),
//            MataKuliah("WKPL B", dhonny, 4, isWorkshop = true),
//            MataKuliah("WSIBW A", sholihah, 4, isWorkshop = true),
//            MataKuliah("WSIBW B", akas, 4, isWorkshop = true),
//            MataKuliah("Matematika Diskrit", dhonny, 2),
//            MataKuliah("Interpesonal Skill", dhonny, 2),
//            MataKuliah("Konsep Jaringan Komputer", rifqi, 2),
//            MataKuliah("Struktur Data", adi, 2),
//            MataKuliah("WMA A", adi, 4, isWorkshop = true),
//            MataKuliah("WMA B", akas, 4, isWorkshop = true),
//            MataKuliah("WSC A", sholihah, sks = 4, isWorkshop = true),
//            MataKuliah("WSC B", akas, 4, isWorkshop = true),
//            MataKuliah("WST A", adi, 4, isWorkshop = true),
//            MataKuliah("WST B", rifqi, 4, isWorkshop = true),
//            MataKuliah("WPCV A", rifqi, sks = 4, isWorkshop = true),
//            MataKuliah("WPCV B", adi, 4, isWorkshop = true),
//            MataKuliah("Sistem Informasi Enterprise", rani, 2),
//            MataKuliah("Sistem Cerdas", sholihah, sks = 2),
//            MataKuliah("Multimedia Permainan", rifqi, sks = 2),
//            MataKuliah("Aplikasi sistem tertanam", adi, 2)
//        )

        val daftarRuangan = listOf(
            Ruangan("Aula", supports = setOf(TipePenggunaan.TEORI, TipePenggunaan.PRAKTIK)),
            Ruangan("Ruang 101", setOf(TipePenggunaan.TEORI, TipePenggunaan.PRAKTIK)),
            Ruangan("Lab RSI", setOf(TipePenggunaan.PRAKTIK)),
            Ruangan("Lab SKK", setOf(TipePenggunaan.PRAKTIK))
        )
        val hari = listOf(
            Hari(
                "Senin",
                jamMulai = 8, jamSelesai = 17,
            ),
            Hari(
                "Selasa",
                jamMulai = 8, jamSelesai = 17,
            ),
            Hari(
                "Rabu",
                jamMulai = 8, jamSelesai = 17,
            ),
            Hari(
                "Kamis",
                jamMulai = 8, jamSelesai = 17,
            ),
            Hari(
                "Jumat",
                jamMulai = 7, jamSelesai = 17,
                jamIstirahatMulai = 11,
                jamIstirahatSelesai = 12
            )
        )


        val jadwal = welchPowellAlgorithm.buatJadwal(
            rawJadwal,
            daftarRuangan,
            hari,
            overrideDurasiWorkshop = workshopTime
        )
        welchPowellAlgorithm.tampilkanJadwal(jadwal.second, daftarRuangan)

        try {
            supabase.from("jadwal")
                .insert(
                    welchPowellAlgorithm.jadwalToJson(
                        jadwal.first,
                        jadwal.second,
                        "SEMESTER ${semester.name.uppercase()}"
                    )
                )
            return Result.success(jadwal.first)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun previewJadwal(semester: Semester): Result<List<PreviewJadwalEntity>> {
        try {
            val list = mutableListOf<PreviewJadwalModel>()

            val mataKuliah = supabase
                .from("mata_kuliah_view").select {
                    order("nama", Order.ASCENDING)
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