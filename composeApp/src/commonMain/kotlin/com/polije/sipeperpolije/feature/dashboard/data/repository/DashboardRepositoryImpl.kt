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
import com.polije.sipeperpolije.feature.dashboard.domain.repository.DashboardRepository
import com.polije.sipeperpolije.feature.master.data.model.MataKuliahModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order

class DashboardRepositoryImpl(private val supabase: SupabaseClient) : DashboardRepository {

    private val welchPowellAlgorithm = WelchPowellAlgorithm()

    override suspend fun logout() {
        supabase.auth.signOut()
    }

    override suspend fun fetchDashboard(): Result<DashboardModel> = runCatching {
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
            )
        )
    }.onFailure {
        return Result.failure(it)
    }

    override suspend fun generateJadwal(semester: Semester): Result<Boolean> {
        val responses = supabase
            .from("mata_kuliah_view").select {
                order("nama", Order.ASCENDING)
            }.decodeList<MataKuliahModel>()
            .filter { semester.matches(it.semester) }

        log("$responses")

        logList("mataKuliah", responses)

        val isMataKuliahValid = responses.all { it.idPengampuPertama != null }

        if (!isMataKuliahValid) {
            return Result.failure(Exception("Ada mata kuliah yang belum memiliki dosen pengampu"))
        }

        val dosenCache = mutableMapOf<Int, Dosen>()

        val rawJadwal = responses.map {
            val dosen = dosenCache.getOrPut(it.idPengampuPertama!!) {
                Dosen(it.idPengampuPertama, it.namaPengampuPertama!!)
            }

            MataKuliah(
                it.nama,
                dosen,
                it.jumlahSKS,
                it.isWorkshop
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
            Ruangan("Aula", canWorkshop = true),
            Ruangan("Ruang 101", canWorkshop = true),
            Ruangan("Lab RSI", canWorkshop = true),
            Ruangan("Lab SKK", canWorkshop = true)
        )
        val hari = listOf(
            Hari(
                "Senin",
                jamPelajaran = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16),
            ),
            Hari(
                "Selasa",
                jamPelajaran = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16),
            ),
            Hari(
                "Rabu",
                jamPelajaran = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16),
            ),
            Hari(
                "Kamis",
                jamPelajaran = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16),
            ),
            Hari(
                "Jumat",
                jamPelajaran = listOf(7, 8, 9, 10, 13, 14, 15, 16),
                jamIstirahat = listOf(11..12)
            )
        )


        val jadwal = welchPowellAlgorithm.buatJadwal(rawJadwal, daftarRuangan, hari)
        welchPowellAlgorithm.tampilkanJadwal(jadwal.second, daftarRuangan)

        try {
            supabase.from("jadwal")
                .insert(welchPowellAlgorithm.jadwalToJson(jadwal.first, jadwal.second))
            return Result.success(jadwal.first)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}