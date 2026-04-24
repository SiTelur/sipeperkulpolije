package com.polije.sipeperpolije.core.algoritm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Dosen(val id: Int, val nama: String)

data class MataKuliah(
    val kode: String,
    val nama: String,
    val dosen: Dosen,
    val sksTeori: Int,
    val sksPraktek: Int,
    val semester: Int,
    val kelas: String = ""
) {
    val isWorkshop: Boolean = (sksPraktek == 4)
    val pertemuanPerMinggu: Int = if (isWorkshop) 2 else 1

    fun getNamaLengkap(): String {
        val kelasLabel = if (kelas.isNotEmpty()) " $kelas" else ""
        return if (pertemuanPerMinggu > 1) {
            "${nama}$kelasLabel"
        } else {
            nama
        }
    }

    val durasiJam: Int = run {
        val teori = sksTeori * DurasiConfig.jamPerSksTeori
        val praktik = if (isWorkshop) {
            (sksPraktek / 2) * DurasiConfig.jamPerSksPraktik
        } else {
            sksPraktek * DurasiConfig.jamPerSksPraktik // 1 SKS Praktek = 2 Jam
        }
        teori + praktik
    }
}

object DurasiConfig {
    var jamPerSksTeori: Int = 1
    var jamPerSksPraktik: Int = 2
}

data class Slot(
    val hari: Hari,
    val jamMulai: Int = hari.jamPelajaran.first(),
    val jamSelesai: Int = hari.jamPelajaran.last()
) {
    override fun toString(): String = "${hari.nama} $jamMulai:00-$jamSelesai:00"
}

@Serializable
enum class TipePenggunaan { TEORI, PRAKTIK }

data class Ruangan(val nama: String, val supports: Set<TipePenggunaan>)

data class Hari(
    val nama: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val jamIstirahatMulai: Int? = null,
    val jamIstirahatSelesai: Int? = null
) {
    init {
        require(
            (jamIstirahatMulai == null && jamIstirahatSelesai == null) ||
                    (jamIstirahatMulai != null && jamIstirahatSelesai != null)
        ) { "Jam istirahat harus diisi lengkap atau tidak sama sekali" }
    }

    val jamIstirahat: IntRange =
        if (jamIstirahatMulai != null && jamIstirahatSelesai != null)
            jamIstirahatMulai..<jamIstirahatSelesai
        else IntRange.EMPTY

    val jamPelajaran: List<Int> =
        (jamMulai..<jamSelesai).filterNot { it in jamIstirahat }
}

data class JadwalItem(
    val mataKuliah: MataKuliah,
    val ruangan: Ruangan,
    val slot: Slot,
    val pertemuanKe: Int = 1,
    val teknisi: Teknisi? = null
) {
    fun getNamaLengkap(): String {
        val kelasLabel = if (mataKuliah.kelas.isNotEmpty()) " ${mataKuliah.kelas}" else ""
        return if (mataKuliah.pertemuanPerMinggu > 1) {
            "${mataKuliah.nama}$kelasLabel (Pertemuan $pertemuanKe)"
        } else {
            mataKuliah.nama
        }
    }
}

data class Teknisi(val id: Int, val nama: String)

@Serializable
data class JadwalDataItem(
    val namaJadwal: String,
    val hari: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val namaDosen: String,
    val semester: Int,
    val sks: Int,
    val namaRuangan: String,
    val namaTeknisi: String? = null
)

@Serializable
data class JadwalPerItem(val nama: String, val items: List<JadwalDataItem>)

@Serializable
data class DosenSummary(
    @SerialName("nama_dosen") val namaDosen: String,
    @SerialName("sks_teori") val sksTeori: Int,
    @SerialName("sks_workshop") val sksWorkshop: Int,
    @SerialName("sks_ajar") val sksAjar: Int,          // sksTeori + sksWorkshop
    @SerialName("beban_sks") val bebanSks: Double,
    @SerialName("total_sesi") val totalSesi: Int
)

@Serializable
data class TeknisiSummary(
    @SerialName("nama_teknisi") val namaTeknisi: String,
    @SerialName("total_sesi") val totalSesi: Int,
    @SerialName("beban_sks") val bebanSks: Int
)

@Serializable
data class Jadwal(
    val title: String,
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("jadwal") val listJadwal: List<JadwalPerItem>,
    @SerialName("jadwal_view") val listJadwalView: List<JadwalPerItem>,
    val semester: String,
    @SerialName("unscheduled_count") val unscheduledCount: Int = 0,
    @SerialName("unscheduled_items") val unscheduledItems: List<UnscheduledItem> = emptyList(),
    val summary: List<DosenSummary> = emptyList(),
    @SerialName("teknisi_summary") val teknisiSummary: List<TeknisiSummary> = emptyList()  // ← tambahkan ini
)

@Serializable
data class UnscheduledItem(
    @SerialName("nama_mk") val namaMk: String,
    val semester: Int,
    val sks: Int,
    @SerialName("nama_dosen") val namaDosen: String,
    @SerialName("pertemuan_ke") val pertemuanKe: Int,
    val alasan: String = "Tidak ada slot/ruangan yang tersedia",
    val degree: Int
)

data class MKDegree(val mk: MataKuliah, val pertemuan: Int, val degree: Int)
data class SlotPriority(val slot: Slot, val priority: Int)

// ─── Conflict Index ───────────────────────────────────────────────────────────

private class ConflictIndex {
    val byHari = HashMap<String, MutableList<JadwalItem>>()
    val byDosen = HashMap<Int, MutableList<JadwalItem>>()
    val byRuangan = HashMap<String, MutableList<JadwalItem>>()
    val bySemester = HashMap<Int, MutableList<JadwalItem>>()
    val byTeknisi = HashMap<Int, MutableList<JadwalItem>>()   // ← tambahkan

    fun add(item: JadwalItem) {
        byHari.getOrPut(item.slot.hari.nama) { mutableListOf() }.add(item)
        byDosen.getOrPut(item.mataKuliah.dosen.id) { mutableListOf() }.add(item)
        byRuangan.getOrPut(item.ruangan.nama) { mutableListOf() }.add(item)
        bySemester.getOrPut(item.mataKuliah.semester) { mutableListOf() }.add(item)
        item.teknisi?.let { t ->                                // ← tambahkan
            byTeknisi.getOrPut(t.id) { mutableListOf() }.add(item)
        }
    }

    fun candidates(mk: MataKuliah, slot: Slot, ruangan: Ruangan): Set<JadwalItem> {
        val result = HashSet<JadwalItem>()
        byHari[slot.hari.nama]?.let { result.addAll(it) }
        byDosen[mk.dosen.id]?.let { result.addAll(it) }
        byRuangan[ruangan.nama]?.let { result.addAll(it) }
        bySemester[mk.semester]?.let { result.addAll(it) }
        return result
    }

    fun remove(item: JadwalItem) {
        byHari[item.slot.hari.nama]?.remove(item)
        byDosen[item.mataKuliah.dosen.id]?.remove(item)
        byRuangan[item.ruangan.nama]?.remove(item)
        bySemester[item.mataKuliah.semester]?.remove(item)
        item.teknisi?.let { t -> byTeknisi[t.id]?.remove(item) }  // ← tambahkan
    }
}


class WelchPowellAlgorithm {
    private lateinit var daftarHari: List<Hari>
    private lateinit var daftarMataKuliah: List<MataKuliah>

    private val slotCache = HashMap<Int, List<Slot>>()

    private fun generateSlotsForDuration(durasi: Int): List<Slot> {
        if (durasi <= 0) return emptyList()
        return slotCache.getOrPut(durasi) {
            buildList {
                for (hariData in daftarHari) {
                    val jamList = hariData.jamPelajaran
                    if (jamList.size < durasi) continue
                    for (i in 0..jamList.size - durasi) {
                        val window = jamList.subList(i, i + durasi)
                        // ✅ OPT: zipWithNext diganti loop manual — lebih cepat
                        var contiguous = true
                        for (j in 0 until window.lastIndex) {
                            if (window[j + 1] != window[j] + 1) {
                                contiguous = false; break
                            }
                        }
                        if (contiguous) add(Slot(hariData, window.first(), window.last() + 1))
                    }
                }
            }
        }
    }

    // ─── Conflict Detection ───────────────────────────────────────────────────

    private fun isTimeOverlap(slot1: Slot, slot2: Slot): Boolean {
        if (slot1.hari != slot2.hari) return false
        return slot1.jamMulai < slot2.jamSelesai && slot2.jamMulai < slot1.jamSelesai
    }

    private fun findTeknisi(
        slot: Slot,
        daftarTeknisi: List<Teknisi>,
        conflictIndex: ConflictIndex
    ): Teknisi? {
        for (teknisi in daftarTeknisi) {
            val sibuk = conflictIndex.byTeknisi[teknisi.id]
                ?.any { item -> isTimeOverlap(item.slot, slot) }
                ?: false
            if (!sibuk) return teknisi
        }
        return null  // semua teknisi sibuk, workshop tetap jalan tanpa teknisi
    }

    private fun getRuanganCocok(
        mk: MataKuliah,
        semuaRuangan: List<Ruangan>
    ): List<Ruangan> {

        return semuaRuangan
            .filter { ruang ->
                when {
                    mk.sksPraktek <= 1 -> TipePenggunaan.TEORI in ruang.supports

                    else -> TipePenggunaan.PRAKTIK in ruang.supports
                }
            }
            .sortedByDescending { ruang ->
                // Jika mata kuliah ini butuh Praktik/Workshop
                if (mk.sksPraktek > 1) {
                    when {
                        // 1. Sangat diprioritaskan: Ruangan khusus Lab (nama ada kata Lab, atau murni cuma bisa praktik)
                        ruang.nama.contains("Lab", ignoreCase = true) -> 4
                        ruang.supports.size == 1 && ruang.supports.contains(TipePenggunaan.PRAKTIK) -> 3
                        // 2. Ruangan hybrid yang bisa praktik
                        ruang.supports.contains(TipePenggunaan.PRAKTIK) -> 2
                        else -> 1
                    }
                } else {
                    // Jika mata kuliah Teori
                    when {
                        // 1. Sangat diprioritaskan: Ruangan murni teori (bukan lab)
                        ruang.supports.size == 1 && ruang.supports.contains(TipePenggunaan.TEORI) -> 3
                        // 2. Ruangan hybrid
                        ruang.supports.size == 2 -> 2
                        else -> 1
                    }
                }
            }
    }

    private fun isConflict(
        mk1: MataKuliah, mk2: MataKuliah,
        slot1: Slot, slot2: Slot,
        ruangan1: Ruangan, ruangan2: Ruangan
    ): Boolean {

        // 1. Jika ini adalah mata kuliah yang SAMA tapi beda pertemuan, HARAM berada di hari yang sama
        if (mk1 == mk2 && slot1.hari == slot2.hari) return true

        // 2. Tidak overlap waktu → aman
        if (!isTimeOverlap(slot1, slot2)) return false

        // 3. Dosen sama → bentrok
        if (mk1.dosen == mk2.dosen) return true

        // 4. Ruangan sama → bentrok
        if (ruangan1 == ruangan2) return true

        // 5. Semester beda → bebas
        if (mk1.semester != mk2.semester) return false

        val mk1Umum = mk1.kelas.isEmpty()
        val mk2Umum = mk2.kelas.isEmpty()

        // 5. Kalau salah satu adalah kelas umum (tidak punya label grup) → bentrok dengan semua kelas lain di semester yang sama
        if (mk1Umum || mk2Umum) return true

        // 6. Keduanya ditujukan untuk grup spesifik
        // beda kelas → BOLEH paralel
        if (mk1.kelas != mk2.kelas) return false

        // kelas sama → bentrok
        return true
    }

    // ─── Degree Computation ───────────────────────────────────────────────────

    /**
     * ✅ OPT: degree dihitung sekali per MK (bukan per pertemuan).
     * ✅ FIX: workshop vs non-workshop semester sama ikut menaikkan degree
     *         karena sekarang mereka saling konflik.
     */
    private fun hitungSemuaDegree(daftarMK: List<MataKuliah>): Map<MataKuliah, Int> {
        val degrees = HashMap<MataKuliah, Int>(daftarMK.size * 2)
        for (mk in daftarMK) {
            var degree = 0
            for (other in daftarMK) {
                if (other === mk) continue

                if (other.dosen.id == mk.dosen.id) {
                    degree += 3 * other.pertemuanPerMinggu
                    continue
                }

                if (mk.isWorkshop && other.isWorkshop) {
                    degree += 1 * other.pertemuanPerMinggu
                    continue
                }

                // Workshop vs teori semester sama = konflik → naikkan degree
                if (mk.semester == other.semester) {
                    degree += other.pertemuanPerMinggu
                }
            }
            degrees[mk] = degree
        }
        return degrees
    }

    // ─── Slot Priority ────────────────────────────────────────────────────────

    /**
     * ✅ OPT: jadwalPerHari di-pass sebagai parameter (di-maintain incremental
     *         di luar loop) sehingga tidak di-rebuild tiap iterasi MK.
     */
    private fun hitungPrioritasSlot(
        slot: Slot,
        mataKuliah: MataKuliah,
        jadwalPerHari: Map<Hari, List<JadwalItem>>,
        pertemuanKe: Int,
        pertemuan1Slot: Slot?
    ): Int {
        var prioritas = 100
        val jadwalHariIni = jadwalPerHari[slot.hari] ?: emptyList()

        if (pertemuanKe == 2 && pertemuan1Slot != null) {
            if (pertemuan1Slot.hari == slot.hari) prioritas -= 50
            else prioritas += 10
        }

        // Filter jadwal yang "berkaitan" (Dosen sama ATAU Mahasiswa yang sama [semester + kelas sama])
        // Termasuk jika salah satu adalah kelas umum (kelas kosong) di semester yang sama
        val jadwalTerkait = jadwalHariIni.filter {
            it.mataKuliah.dosen == mataKuliah.dosen ||
                    (it.mataKuliah.semester == mataKuliah.semester &&
                            (it.mataKuliah.kelas == mataKuliah.kelas || it.mataKuliah.kelas.isEmpty() || mataKuliah.kelas.isEmpty()))
        }

        if (jadwalTerkait.isNotEmpty()) {
            prioritas += 20 // Bonus karena jadwal berada di hari yang sudah ada target mahasiswa/dosen

            // Penalti agar kelas dari SATU GRUP (atau dosen) tidak terlalu menumpuk/overload di 1 hari (>3 kelas)
            if (jadwalTerkait.size >= 3) {
                prioritas -= (jadwalTerkait.size - 2) * 20
            } else {
                prioritas -= jadwalTerkait.size * 5
            }
        } else {
            // Penalti sedang jika membuka HARI BARU untuk mahasiswa/dosen ini.
            prioritas -= 15
        }

        // --- GLOBAL WORKSHOP DISTRIBUTION --- 
        // Jika ini adalah kelas Workshop, kita SANGAT MENCEGAH agar tidak menumpuk di hari yang sama 
        // dengan workshop lain (bahkan untuk dosen/mahasiswa beda) supaya ruangan/hari terdistribusi merata hingga Kamis/Jumat.
        if (mataKuliah.isWorkshop) {
            val totalWorkshopHariIni = jadwalHariIni.count { it.mataKuliah.isWorkshop }
            prioritas -= totalWorkshopHariIni * 50 // Penalti brutal setiap kali nambah workshop di hari yang sama
        } else {
            // Penalti ringan untuk kelas biasa meratakan ruang
            prioritas -= jadwalHariIni.size * 2
        }

        // SNAP TO GRID: Beri bonus agar kelas SELALU menempel pada pinggir pagi atau pasca istirahat
        val isAwalHari = slot.jamMulai == slot.hari.jamPelajaran.first()
        val isSetelahIstirahat =
            slot.hari.jamIstirahat != IntRange.EMPTY && slot.jamMulai == slot.hari.jamIstirahat.last + 1
        if (isAwalHari || isSetelahIstirahat) {
            prioritas += 10
        }

        // 2. Bonus "back-to-back" / jadwal nempel
        for (existing in jadwalTerkait) {
            val isMemangNempel =
                existing.slot.jamSelesai == slot.jamMulai || slot.jamSelesai == existing.slot.jamMulai

            val br = slot.hari.jamIstirahat
            val isNempelKarenaIstirahat = if (br != IntRange.EMPTY) {
                (existing.slot.jamSelesai == br.first && slot.jamMulai == br.last + 1) ||
                        (slot.jamSelesai == br.first && existing.slot.jamMulai == br.last + 1)
            } else false

            if (isMemangNempel || isNempelKarenaIstirahat) {
                prioritas += 35 // Reward sangat tinggi untuk jadwal nempel
            }
        }

        val allStarts = buildList {
            jadwalTerkait.forEach { add(it.slot.jamMulai to it.slot.jamSelesai) }
            add(slot.jamMulai to slot.jamSelesai)
        }.sortedBy { it.first }

        var gapPenalty = 0
        for (i in 0 until allStarts.lastIndex) {
            val end = allStarts[i].second
            val start = allStarts[i + 1].first
            if (start > end) {
                val br = slot.hari.jamIstirahat
                val breakInGap = if (br != IntRange.EMPTY)
                    (maxOf(end, br.first) until minOf(start, br.last + 1)).count()
                else 0
                gapPenalty += (start - end - breakInGap).coerceAtLeast(0) * 10 // Penalti lubang waktu = 10 per jam
            }
        }
        prioritas -= gapPenalty

        if (jadwalTerkait.isNotEmpty()) {
            val minStart = minOf(slot.jamMulai, jadwalTerkait.minOf { it.slot.jamMulai })
            val maxEnd = maxOf(slot.jamSelesai, jadwalTerkait.maxOf { it.slot.jamSelesai })
            val usedTime = (slot.jamSelesai - slot.jamMulai) +
                    jadwalTerkait.sumOf { it.slot.jamSelesai - it.slot.jamMulai }
            val span = (maxEnd - minStart).coerceAtLeast(1)
            prioritas += (usedTime * 100 / span) / 8 // Bonus kepadatan waktu harian
        }

        return prioritas
    }

    // ─── Main Scheduling ─────────────────────────────────────────────────────

    fun buatJadwal(
        daftarMataKuliah: List<MataKuliah>,
        daftarRuangan: List<Ruangan>,
        daftarHari: List<Hari>,
        daftarTeknisi: List<Teknisi>,   // ← tambahkan, default kosong agar backward-compatible
        overrideDurasiWorkshop: Int? = null
    ): Triple<Boolean, List<JadwalItem>, List<UnscheduledItem>> {
        this.daftarMataKuliah = daftarMataKuliah
        this.daftarHari = daftarHari
        slotCache.clear()

        val degreeMap = hitungSemuaDegree(daftarMataKuliah)

        val expandedMK = daftarMataKuliah.flatMap { mk ->
            (1..mk.pertemuanPerMinggu).map { MKDegree(mk, it, degreeMap[mk] ?: 0) }
        }.sortedWith(
            compareByDescending<MKDegree> { it.mk.durasiJam } // 🔥 1. Prioritaskan kelas berdurasi panjang (4 jam) terlebih dahulu agar blok rapi
                .thenByDescending { it.degree }               // 🔥 2. Jika durasi seri, atasi yang paling rentan konflik duluan
                .thenByDescending { it.mk.kelas.isEmpty() }   // 🔥 3. Atasi kelas umum
        )

        println("Urutan mata kuliah berdasarkan degree:")
        expandedMK.forEach { (mk, pertemuan, degree) ->
            val suffix = if (mk.pertemuanPerMinggu > 1) " (Pertemuan $pertemuan)" else ""
            println("  ${mk.getNamaLengkap()}$suffix — Degree: $degree, Durasi: ${mk.durasiJam} jam")
        }
        println()


        val jadwal = mutableListOf<JadwalItem>()
        val conflictIndex = ConflictIndex()
        var isSuccess = true
        val unscheduledList = mutableListOf<UnscheduledItem>()

        // ✅ OPT: jadwalPerHari di-maintain incremental — tidak di-rebuild tiap MK
        val jadwalPerHari = HashMap<Hari, MutableList<JadwalItem>>()

        // ✅ OPT: index pertemuan-1 workshop agar tidak scan list tiap iterasi
        val pertemuan1SlotMap = HashMap<MataKuliah, Slot>()

        for ((mataKuliah, pertemuanKe, degree) in expandedMK) {
            val durasi = if (mataKuliah.isWorkshop && overrideDurasiWorkshop != null)
                overrideDurasiWorkshop else mataKuliah.durasiJam

            val availableSlots = generateSlotsForDuration(durasi)
            if (availableSlots.isEmpty()) {
                println("⚠ Tidak ada slot tersedia untuk durasi ${mataKuliah.durasiJam} jam!")
                isSuccess = false
                unscheduledList.add(
                    UnscheduledItem(
                        namaMk = mataKuliah.getNamaLengkap(),
                        semester = mataKuliah.semester,
                        sks = mataKuliah.sksTeori + mataKuliah.sksPraktek,
                        namaDosen = mataKuliah.dosen.nama,
                        pertemuanKe = pertemuanKe,
                        alasan = "Tidak ada slot tersedia untuk durasi ${mataKuliah.durasiJam} jam",
                        degree = degree
                    )
                )
                continue
            }

            // ✅ OPT: langsung ambil dari map, tidak perlu scan jadwal list
            val pertemuan1Slot = if (pertemuanKe == 2) pertemuan1SlotMap[mataKuliah] else null

            val slotsWithPriority = availableSlots
                .map { slot ->
                    SlotPriority(
                        slot,
                        hitungPrioritasSlot(
                            slot,
                            mataKuliah,
                            jadwalPerHari,
                            pertemuanKe,
                            pertemuan1Slot
                        )
                    )
                }
                .sortedWith(
                    compareBy<SlotPriority> { item ->
                        daftarHari.indexOfFirst {
                            it.nama.equals(
                                item.slot.hari.nama,
                                ignoreCase = true
                            )
                        }
                    }.thenBy { it.slot.jamMulai }
                        .thenByDescending { it.priority } // Heuristic sebagai tie-breaker
                )

            val ruanganCocok = getRuanganCocok(mataKuliah, daftarRuangan)
            var berhasil = false

            outer@ for ((slot, _) in slotsWithPriority) {
                for (ruangan in ruanganCocok) {
                    val adaKonflik = conflictIndex
                        .candidates(mataKuliah, slot, ruangan)
                        .any { item ->
                            isConflict(
                                mataKuliah, item.mataKuliah,
                                slot, item.slot,
                                ruangan, item.ruangan
                            )
                        }

                    if (!adaKonflik) {
                        // ← assign teknisi hanya untuk workshop
                        val teknisiAssigned = if (mataKuliah.isWorkshop) {
                            findTeknisi(slot, daftarTeknisi, conflictIndex)
                        } else null

                        val newItem =
                            JadwalItem(mataKuliah, ruangan, slot, pertemuanKe, teknisiAssigned)
                        jadwal.add(newItem)
                        conflictIndex.add(newItem)
                        jadwalPerHari.getOrPut(slot.hari) { mutableListOf() }.add(newItem)

                        if (pertemuanKe == 1 && mataKuliah.isWorkshop) {
                            pertemuan1SlotMap[mataKuliah] = slot
                        }

                        berhasil = true
                        val suffix =
                            if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                        val teknisiLog = teknisiAssigned?.let { " | Teknisi: ${it.nama}" } ?: ""
                        println("✓ ${mataKuliah.nama}$suffix → ${slot.hari.nama} ${slot.jamMulai}:00-${slot.jamSelesai}:00 | ${ruangan.nama}$teknisiLog")
                        break@outer
                    }
                }
            }

            // ✅ FIX: hanya satu blok if (!berhasil) — duplikat dihapus
            if (!berhasil) {
                isSuccess = false
                val suffix =
                    if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                println("✗ GAGAL: ${mataKuliah.nama}$suffix tidak bisa dijadwalkan!")
                unscheduledList.add(
                    UnscheduledItem(
                        namaMk = mataKuliah.getNamaLengkap(),
                        semester = mataKuliah.semester,
                        sks = mataKuliah.sksTeori + mataKuliah.sksPraktek,
                        namaDosen = mataKuliah.dosen.nama,
                        pertemuanKe = pertemuanKe,
                        alasan = "Konflik tidak dapat diselesaikan (dosen/ruangan/semester)",
                        degree = degree
                    )
                )
            }
        }

        // --- COMPACTION PASS (Left-Shift) ---
        // Mencoba memindahkan jadwal yang sudah jadi ke hari/jam yang lebih awal jika ada ruangan kosong.
        var adaPerubahan = true
        var compactionLoop = 0
        while (adaPerubahan && compactionLoop < 3) { // Max 3 shift passes
            adaPerubahan = false
            compactionLoop++

            // Urutkan dari yang paling akhir untuk digeser ke awal
            val snapshotJadwal = jadwal.sortedWith(
                compareByDescending<JadwalItem> { item ->
                    daftarHari.indexOfFirst {
                        it.nama.equals(
                            item.slot.hari.nama,
                            ignoreCase = true
                        )
                    }
                }.thenByDescending { it.slot.jamMulai }
            )

            for (item in snapshotJadwal) {
                // Cabut sementara item
                jadwal.remove(item)
                conflictIndex.remove(item)
                jadwalPerHari[item.slot.hari]?.remove(item)

                val durasi = item.slot.jamSelesai - item.slot.jamMulai
                val availableSlots = generateSlotsForDuration(durasi)
                val ruanganCocok = getRuanganCocok(item.mataKuliah, daftarRuangan)

                var slotBaruDitemukan: JadwalItem? = null

                searchSlot@ for (kandidatSlot in availableSlots) {
                    val candHariIdx = daftarHari.indexOfFirst {
                        it.nama.equals(
                            kandidatSlot.hari.nama,
                            ignoreCase = true
                        )
                    }
                    val currHariIdx = daftarHari.indexOfFirst {
                        it.nama.equals(
                            item.slot.hari.nama,
                            ignoreCase = true
                        )
                    }

                    // Hanya target slot yg benar-benar LUAR BIASA lebih awal
                    if (candHariIdx > currHariIdx) continue
                    if (candHariIdx == currHariIdx && kandidatSlot.jamMulai >= item.slot.jamMulai) continue

                    // Kalau praktek > 1 pertemuan per minggu, jangan sampai nimpa hari yg sama
                    var melanggarHariSama = false
                    if (item.mataKuliah.pertemuanPerMinggu > 1) {
                        melanggarHariSama = jadwal.any {
                            it.mataKuliah == item.mataKuliah && it.slot.hari == kandidatSlot.hari
                        }
                    }
                    if (melanggarHariSama) continue

                    for (kandidatRuangan in ruanganCocok) {
                        val adaKonflik = conflictIndex
                            .candidates(item.mataKuliah, kandidatSlot, kandidatRuangan)
                            .any { existing ->
                                isConflict(
                                    item.mataKuliah, existing.mataKuliah,
                                    kandidatSlot, existing.slot,
                                    kandidatRuangan, existing.ruangan
                                )
                            }

                        if (!adaKonflik) {
                            // Re-assign teknisi di slot baru
                            val teknisiAssigned = if (item.mataKuliah.isWorkshop) {
                                findTeknisi(kandidatSlot, daftarTeknisi, conflictIndex)
                            } else null

                            slotBaruDitemukan = JadwalItem(
                                item.mataKuliah,
                                kandidatRuangan,
                                kandidatSlot,
                                item.pertemuanKe,
                                teknisiAssigned   // ← tambahkan
                            )
                            break@searchSlot
                        }
                    }
                }

                val targetItem = slotBaruDitemukan ?: item
                if (slotBaruDitemukan != null) {
                    adaPerubahan = true
                    println("➜ COMPACTION PASS: Menggeser ${item.getNamaLengkap()} dari ${item.slot.hari.nama} ${item.slot.jamMulai} ke ${targetItem.slot.hari.nama} ${targetItem.slot.jamMulai}")
                }

                jadwal.add(targetItem)
                conflictIndex.add(targetItem)
                jadwalPerHari.getOrPut(targetItem.slot.hari) { mutableListOf() }.add(targetItem)
            }
        }

        val jadwalFinal = jadwal.sortedWith(
            compareBy<JadwalItem> { item ->
                daftarHari.indexOfFirst { it.nama.equals(item.slot.hari.nama, ignoreCase = true) }
            }.thenBy { it.slot.jamMulai }
        )

        return Triple(isSuccess, jadwalFinal, unscheduledList)
    }

    // ─── Display ─────────────────────────────────────────────────────────────

    fun tampilkanJadwal(jadwal: List<JadwalItem>, daftarRuangan: List<Ruangan>) {
        println("=".repeat(80))
        println("JADWAL MATA KULIAH")
        println("=".repeat(80))

        for (hari in daftarHari) {
            val jadwalHari = jadwal.filter { it.slot.hari.nama == hari.nama }
                .sortedBy { it.slot.jamMulai }
            val jamIstirahatStr = if (hari.jamIstirahat != IntRange.EMPTY)
                "${hari.jamIstirahat.first}:00-${hari.jamIstirahat.last + 1}:00"
            else "—"

            println("\n${hari.nama} (${jadwalHari.size} sesi) — Istirahat: $jamIstirahatStr")
            println("-".repeat(60))

            if (jadwalHari.isEmpty()) {
                println("  Tidak ada jadwal")
            } else {
                jadwalHari.forEach { item ->
                    println(
                        "  ${item.slot.jamMulai}:00-${item.slot.jamSelesai}:00 | " +
                                "${item.ruangan.nama.padEnd(14)} | " +
                                "${item.getNamaLengkap().padEnd(32)} | " +
                                "${item.mataKuliah.dosen.nama} " +
                                "(${item.mataKuliah.sksTeori + item.mataKuliah.sksPraktek} SKS)"
                    )
                }
                analyzeTimeGaps(jadwalHari, hari)
            }
        }

        println("\n" + "=".repeat(80))
        println("STATISTIK")
        println("=".repeat(80))
        println("Total sesi: ${jadwal.size}")
        println(
            "MK unik: ${
                jadwal.map { it.mataKuliah.nama }.toSet().size
            } / ${daftarMataKuliah.size}"
        )

        println("\nDistribusi per hari:")
        daftarHari.forEach { hari ->
            println("  ${hari.nama}: ${jadwal.count { it.slot.hari.nama == hari.nama }} sesi")
        }

        println("\nPenggunaan ruangan:")
        daftarRuangan.forEach { r ->
            println("  ${r.nama}: ${jadwal.count { it.ruangan == r }} sesi")
        }

        println("\nBeban dosen:")
        jadwal.groupBy { it.mataKuliah.dosen.nama }.forEach { (dosen, items) ->
            val sks = items.map { it.mataKuliah }.toSet().sumOf { it.sksTeori + it.sksPraktek }
            println("  $dosen: ${items.size} sesi, $sks SKS")
        }

        println("\nStatus per MK:")
        daftarMataKuliah.forEach { mk ->
            val terjadwal = jadwal.count { it.mataKuliah == mk }
            val status = when {
                terjadwal == mk.pertemuanPerMinggu -> "✓"
                terjadwal > 0 -> "⚠ tidak lengkap"
                else -> "✗ belum terjadwal"
            }
            println("  $status ${mk.nama}: $terjadwal/${mk.pertemuanPerMinggu}")
        }
    }

    private fun analyzeTimeGaps(jadwalHari: List<JadwalItem>, hari: Hari) {
        val sorted = jadwalHari.sortedBy { it.slot.jamMulai }
        val gaps = mutableListOf<Triple<Int, Int, Int>>()

        fun addGap(start: Int, end: Int) {
            if (start >= end) return
            val br = hari.jamIstirahat
            if (br != IntRange.EMPTY && start < br.last + 1 && end > br.first) {
                if (start < br.first) gaps.add(Triple(start, br.first, br.first - start))
                if (end > br.last + 1) gaps.add(Triple(br.last + 1, end, end - br.last - 1))
            } else {
                gaps.add(Triple(start, end, end - start))
            }
        }

        val firstJam = hari.jamPelajaran.first()
        val lastJam = hari.jamPelajaran.last() + 1

        addGap(firstJam, sorted.first().slot.jamMulai)
        for (i in 0 until sorted.lastIndex)
            addGap(sorted[i].slot.jamSelesai, sorted[i + 1].slot.jamMulai)
        addGap(sorted.last().slot.jamSelesai, lastJam)

        if (gaps.isNotEmpty()) {
            val total = gaps.sumOf { it.third }
            println("  ⚡ Celah total: $total jam")
            gaps.forEach { (s, e, d) ->
                val mark = when {
                    d >= 4 -> "🔴"; d >= 2 -> "🟡"; else -> "🟢"
                }
                println("    $mark $s:00-$e:00 ($d jam)")
            }
        } else {
            println("  ✅ Tidak ada celah — optimal!")
        }

        val usedTime = sorted.sumOf { it.slot.jamSelesai - it.slot.jamMulai }
        val span = sorted.last().slot.jamSelesai - sorted.first().slot.jamMulai
        val brInSpan = if (hari.jamIstirahat != IntRange.EMPTY) {
            (maxOf(sorted.first().slot.jamMulai, hari.jamIstirahat.first)
                    until minOf(sorted.last().slot.jamSelesai, hari.jamIstirahat.last + 1)).count()
        } else 0
        val eff = if (span - brInSpan > 0) usedTime * 100 / (span - brInSpan) else 100
        println("  📊 Efisiensi: $usedTime/${span - brInSpan} jam ($eff%)")
    }

    // ─── JSON Conversion ─────────────────────────────────────────────────────

    fun jadwalToJson(
        title: String,
        isSuccess: Boolean,
        jadwal: List<JadwalItem>,
        semester: String,
        daftarMataKuliah: List<MataKuliah>,
        referensiRombel: List<MataKuliah>,
        unscheduledItems: List<UnscheduledItem> = emptyList()
    ): Jadwal {

        val jadwalData = jadwal.map { item ->
            JadwalDataItem(
                namaJadwal = item.getNamaLengkap(),
                hari = item.slot.hari.nama,
                jamMulai = item.slot.jamMulai,
                jamSelesai = item.slot.jamSelesai,
                namaDosen = item.mataKuliah.dosen.nama,
                semester = item.mataKuliah.semester,
                sks = item.mataKuliah.sksTeori + item.mataKuliah.sksPraktek,
                namaRuangan = item.ruangan.nama,
                namaTeknisi = item.teknisi?.nama   // ← tambahkan
            )
        }

        val groupedByRuangan = jadwalData
            .groupBy { it.namaRuangan }
            .entries.sortedBy { it.key }
            .map { (ruangan, items) ->
                JadwalPerItem(
                    nama = ruangan,
                    items = items.sortedWith(
                        compareBy(
                            { HARI_ORDER[it.hari.lowercase()] ?: Int.MAX_VALUE },
                            { it.jamMulai }
                        )
                    )
                )
            }

        val dosenPerKode: Map<String, Int> = daftarMataKuliah
            .groupBy { it.kode }
            .mapValues { (_, mks) -> mks.map { it.dosen.id }.distinct().count().coerceAtLeast(1) }

        // Pakai referensiRombel (semua semester) bukan daftarMataKuliah
        val kelasPerSemesterRaw: Map<Int, Int> = referensiRombel
            .filter { it.kelas.isNotEmpty() }
            .groupBy { it.semester }
            .mapValues { (_, mks) -> mks.map { it.kelas.uppercase().trim() }.distinct().count() }

        fun jumlahRombelSemester(semester: Int): Int {
            // Cek semester ini dulu
            kelasPerSemesterRaw[semester]?.let { if (it > 0) return it }

            // Cek semester pasangan (genap ↔ ganjil, selisih 1)
            val pasangan = if (semester % 2 == 0) semester - 1 else semester + 1
            kelasPerSemesterRaw[pasangan]?.let { if (it > 0) return it }

            // Fallback
            return 1
        }

        val jumlahKelasPerKode: Map<String, Int> = daftarMataKuliah
            .groupBy { it.kode }
            .mapValues { (_, mks) ->
                val kelasList = mks.map { it.kelas }.filter { it.isNotEmpty() }.distinct()
                if (kelasList.isEmpty()) {
                    // Matkul tanpa label kelas (teori umum) → pakai rombel dari semesternya
                    jumlahRombelSemester(mks.first().semester)
                } else {
                    kelasList.size
                }
            }

        val dosenSummary = jadwal
            .groupBy { it.mataKuliah.dosen }
            .entries.sortedBy { it.key.nama }
            .map { (dosen, items) ->
                var sksAjarTeori = 0
                var sksAjarPraktek = 0
                var bebanSksTotal = 0.0

                val myUniqueMks = items.map { it.mataKuliah }.distinct()

                for (mk in myUniqueMks) {
                    val kode = mk.kode
                    val kls = mk.kelas

                    val totalDosenSatuMatkul = dosenPerKode[kode] ?: 1

                    if (kls.isEmpty()) {
                        val pengali = jumlahKelasPerKode[kode] ?: 1
                        sksAjarTeori += mk.sksTeori * pengali
                        sksAjarPraktek += mk.sksPraktek * pengali

                        bebanSksTotal += (mk.sksTeori * pengali).toDouble() / totalDosenSatuMatkul
                        bebanSksTotal += (mk.sksPraktek * pengali).toDouble() / totalDosenSatuMatkul
                    } else {
                        sksAjarTeori += mk.sksTeori
                        sksAjarPraktek += mk.sksPraktek

                        bebanSksTotal += mk.sksTeori.toDouble() / totalDosenSatuMatkul
                        bebanSksTotal += mk.sksPraktek.toDouble() / totalDosenSatuMatkul
                    }
                }

                val sksAjarTotal = sksAjarTeori + sksAjarPraktek

                DosenSummary(
                    namaDosen = dosen.nama,
                    sksTeori = sksAjarTeori,
                    sksWorkshop = sksAjarPraktek,
                    sksAjar = sksAjarTotal,
                    bebanSks = bebanSksTotal,
                    totalSesi = items.size
                )
            }

        val teknisiSummary = jadwal
            .filter { it.teknisi != null }
            .groupBy { it.teknisi!! }
            .entries.sortedBy { it.key.nama }
            .map { (teknisi, items) ->
                val mkDipegang = items.map { it.mataKuliah }.toSet()

                val bebanSks = mkDipegang.sumOf { mk ->
                    mk.sksPraktek  // jumlah kelas sudah terwakili karena tiap kelas = 1 MataKuliah berbeda
                }

                TeknisiSummary(
                    namaTeknisi = teknisi.nama,
                    totalSesi = items.size,
                    bebanSks = bebanSks
                )
            }


        val groupedByHari = jadwalData
            .groupBy { it.hari }
            .entries.sortedBy { (hari, _) -> HARI_ORDER[hari.lowercase()] ?: Int.MAX_VALUE }
            .map { (hari, items) -> JadwalPerItem(hari, items) }

        return Jadwal(
            title, isSuccess, groupedByRuangan, groupedByHari, semester,
            unscheduledItems.size, unscheduledItems, if (isSuccess) dosenSummary else emptyList(),
            if (isSuccess) teknisiSummary else emptyList()   // ← tambahkan
        )
    }


    companion object {
        private val HARI_ORDER = mapOf(
            "senin" to 1, "selasa" to 2, "rabu" to 3,
            "kamis" to 4, "jumat" to 5, "sabtu" to 6, "minggu" to 7
        )
    }
}