package com.polije.sipeperpolije.core.algoritm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Dosen(val id: Int, val nama: String)

data class MataKuliah(
    val nama: String,
    val dosen: Dosen,
    val sksTeori: Int,
    val sksPraktek: Int,
    val semester: Int,
) {
    val isWorkshop: Boolean = (sksPraktek == 4)
    val pertemuanPerMinggu: Int = if (isWorkshop) 2 else 1

    // Key unik untuk identifikasi kelompok mahasiswa yang sama

    val kelas = nama.split(" ").map { it.first() }.joinToString("")

    val kelasKey: String = "$semester-$kelas"  // e.g. "1-A", "1-B", "1-"

    val durasiJam: Int = run {
        val teori = sksTeori * DurasiConfig.jamPerSksTeori
        val praktik =
            (if (sksPraktek > 2) sksPraktek / 2 else sksPraktek) * DurasiConfig.jamPerSksPraktik
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
    val pertemuanKe: Int = 1
) {
    fun getNamaLengkap(): String {
        return if (mataKuliah.pertemuanPerMinggu > 1) {
            "${mataKuliah.nama} (Pertemuan $pertemuanKe)"
        } else {
            mataKuliah.nama
        }
    }
}

@Serializable
data class JadwalDataItem(
    val namaJadwal: String,
    val hari: String,
    val jamMulai: Int,
    val jamSelesai: Int,
    val namaDosen: String,
    val semester: Int,
    val sks: Int,
    val namaRuangan: String
)

@Serializable
data class JadwalPerItem(val nama: String, val items: List<JadwalDataItem>)

@Serializable
data class Jadwal(
    val title: String,
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("jadwal") val listJadwal: List<JadwalPerItem>,
    @SerialName("jadwal_view") val listJadwalView: List<JadwalPerItem>,
    val semester: String,
    @SerialName("unscheduled_count") val unscheduledCount: Int = 0,
    @SerialName("unscheduled_items") val unscheduledItems: List<UnscheduledItem> = emptyList()
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

    fun add(item: JadwalItem) {
        byHari.getOrPut(item.slot.hari.nama) { mutableListOf() }.add(item)
        byDosen.getOrPut(item.mataKuliah.dosen.id) { mutableListOf() }.add(item)
        byRuangan.getOrPut(item.ruangan.nama) { mutableListOf() }.add(item)
        bySemester.getOrPut(item.mataKuliah.semester) { mutableListOf() }.add(item)
    }

    fun candidates(mk: MataKuliah, slot: Slot, ruangan: Ruangan): Set<JadwalItem> {
        val result = HashSet<JadwalItem>()
        byHari[slot.hari.nama]?.let { result.addAll(it) }
        byDosen[mk.dosen.id]?.let { result.addAll(it) }
        byRuangan[ruangan.nama]?.let { result.addAll(it) }
        bySemester[mk.semester]?.let { result.addAll(it) }
        return result
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

    private fun isConflict(
        mk1: MataKuliah, mk2: MataKuliah,
        slot1: Slot, slot2: Slot,
        ruangan1: Ruangan, ruangan2: Ruangan
    ): Boolean {
        if (mk1 == mk2 && mk1.isWorkshop && slot1.hari == slot2.hari) return true

        if (!isTimeOverlap(slot1, slot2)) return false

        if (mk1.dosen == mk2.dosen) return true
        if (ruangan1 == ruangan2) return true

        if (mk1.semester == mk2.semester) {
            // Workshop vs workshop = boleh paralel (beda mahasiswa, beda ruangan)
            if (mk1.isWorkshop && mk2.isWorkshop) return false
            // Workshop vs teori, atau teori vs teori = KONFLIK
            return true
        }

        return false
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
        jadwalPerHari: Map<Hari, List<JadwalItem>>,
        pertemuanKe: Int,
        pertemuan1Slot: Slot?
    ): Int {
        var prioritas = 100
        val jadwalHariIni = jadwalPerHari[slot.hari] ?: emptyList()

        prioritas -= jadwalHariIni.size * 3

        if (pertemuanKe == 2 && pertemuan1Slot != null) {
            if (pertemuan1Slot.hari == slot.hari) prioritas -= 50
            else prioritas += 10
        }

        for (existing in jadwalHariIni) {
            if (existing.slot.jamSelesai == slot.jamMulai) prioritas += 25
            if (slot.jamSelesai == existing.slot.jamMulai) prioritas += 25
        }

        if (jadwalHariIni.isEmpty() && slot.jamMulai == slot.hari.jamPelajaran.first()) {
            prioritas += 20
        }

        val allStarts = buildList {
            jadwalHariIni.forEach { add(it.slot.jamMulai to it.slot.jamSelesai) }
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
                gapPenalty += (start - end - breakInGap).coerceAtLeast(0) * 8
            }
        }
        prioritas -= gapPenalty

        if (jadwalHariIni.isNotEmpty()) {
            val minStart = minOf(slot.jamMulai, jadwalHariIni.minOf { it.slot.jamMulai })
            val maxEnd = maxOf(slot.jamSelesai, jadwalHariIni.maxOf { it.slot.jamSelesai })
            val usedTime = (slot.jamSelesai - slot.jamMulai) +
                    jadwalHariIni.sumOf { it.slot.jamSelesai - it.slot.jamMulai }
            val span = (maxEnd - minStart).coerceAtLeast(1)
            prioritas += (usedTime * 100 / span) / 10
        }

        return prioritas
    }

    // ─── Main Scheduling ─────────────────────────────────────────────────────

    fun buatJadwal(
        daftarMataKuliah: List<MataKuliah>,
        daftarRuangan: List<Ruangan>,
        daftarHari: List<Hari>,
        overrideDurasiWorkshop: Int? = null
    ): Triple<Boolean, List<JadwalItem>, List<UnscheduledItem>> {
        this.daftarMataKuliah = daftarMataKuliah
        this.daftarHari = daftarHari
        slotCache.clear()

        val degreeMap = hitungSemuaDegree(daftarMataKuliah)

        val expandedMK = daftarMataKuliah.flatMap { mk ->
            (1..mk.pertemuanPerMinggu).map { MKDegree(mk, it, degreeMap[mk] ?: 0) }
        }.sortedWith(
            compareByDescending<MKDegree> { it.degree }
                .thenByDescending { it.mk.durasiJam }
                .thenByDescending { it.mk.isWorkshop }
        )

        println("Urutan mata kuliah berdasarkan degree:")
        expandedMK.forEach { (mk, pertemuan, degree) ->
            val suffix = if (mk.pertemuanPerMinggu > 1) " (Pertemuan $pertemuan)" else ""
            println("  ${mk.nama}$suffix — Degree: $degree, Durasi: ${mk.durasiJam} jam")
        }
        println()

        val ruanganTeori = daftarRuangan
            .filter { TipePenggunaan.TEORI in it.supports }
            .sortedBy { if (it.supports.size == 1) 0 else 1 }
        val ruanganPraktik = daftarRuangan
            .filter { TipePenggunaan.PRAKTIK in it.supports }
            .sortedBy { if (it.supports.size == 1) 0 else 1 }

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
                        namaMk = mataKuliah.nama,
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
                        hitungPrioritasSlot(slot, jadwalPerHari, pertemuanKe, pertemuan1Slot)
                    )
                }
                .sortedByDescending { it.priority }

            val ruanganCocok = if (mataKuliah.isWorkshop) ruanganPraktik else ruanganTeori
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
                        val newItem = JadwalItem(mataKuliah, ruangan, slot, pertemuanKe)
                        jadwal.add(newItem)
                        conflictIndex.add(newItem)

                        // ✅ OPT: update incremental map
                        jadwalPerHari.getOrPut(slot.hari) { mutableListOf() }.add(newItem)

                        // ✅ OPT: simpan slot pertemuan-1 untuk workshop
                        if (pertemuanKe == 1 && mataKuliah.isWorkshop) {
                            pertemuan1SlotMap[mataKuliah] = slot
                        }

                        berhasil = true
                        val suffix =
                            if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                        println("✓ ${mataKuliah.nama}$suffix → ${slot.hari.nama} ${slot.jamMulai}:00-${slot.jamSelesai}:00 | ${ruangan.nama}")
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
                        namaMk = mataKuliah.nama,
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
                namaRuangan = item.ruangan.nama
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

        val groupedByHari = jadwalData
            .groupBy { it.hari }
            .entries.sortedBy { (hari, _) -> HARI_ORDER[hari.lowercase()] ?: Int.MAX_VALUE }
            .map { (hari, items) -> JadwalPerItem(hari, items) }

        return Jadwal(
            title, isSuccess, groupedByRuangan, groupedByHari, semester,
            unscheduledItems.size, unscheduledItems
        )
    }

    companion object {
        private val HARI_ORDER = mapOf(
            "senin" to 1, "selasa" to 2, "rabu" to 3,
            "kamis" to 4, "jumat" to 5, "sabtu" to 6, "minggu" to 7
        )
    }
}