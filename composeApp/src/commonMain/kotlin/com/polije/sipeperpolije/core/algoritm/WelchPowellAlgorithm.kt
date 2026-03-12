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

    // Changed from a getter (recomputed every access) to a val (computed once)
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
data class Jadwal(
    val title: String,
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("jadwal") val listJadwal: List<JadwalPerItem>,
    @SerialName("jadwal_view") val listJadwalView: List<JadwalPerItem>,
    val semester: String
)

@Serializable
data class JadwalPerItem(val nama: String, val items: List<JadwalDataItem>)

data class MKDegree(val mk: MataKuliah, val pertemuan: Int, val degree: Int)
data class SlotPriority(val slot: Slot, val priority: Int)

// ─── Conflict index for O(1) lookups ─────────────────────────────────────────
/**
 * Tracks scheduled items by three axes so conflict checks avoid a full list scan.
 * Each key maps to a list of JadwalItems that could conflict on that axis.
 */
private class ConflictIndex {
    // hari.nama → items scheduled that day
    val byHari = HashMap<String, MutableList<JadwalItem>>()

    // dosen.id → items for that dosen
    val byDosen = HashMap<Int, MutableList<JadwalItem>>()

    // ruangan.nama → items in that room
    val byRuangan = HashMap<String, MutableList<JadwalItem>>()

    // semester → items for that semester (non-workshop only)
    val bySemester = HashMap<Int, MutableList<JadwalItem>>()

    fun add(item: JadwalItem) {
        byHari.getOrPut(item.slot.hari.nama) { mutableListOf() }.add(item)
        byDosen.getOrPut(item.mataKuliah.dosen.id) { mutableListOf() }.add(item)
        byRuangan.getOrPut(item.ruangan.nama) { mutableListOf() }.add(item)
        if (!item.mataKuliah.isWorkshop)
            bySemester.getOrPut(item.mataKuliah.semester) { mutableListOf() }.add(item)
    }

    /** Returns only the candidates that could realistically conflict with (mk, slot, ruangan). */
    fun candidates(mk: MataKuliah, slot: Slot, ruangan: Ruangan): Set<JadwalItem> {
        val result = HashSet<JadwalItem>()
        // Same day — needed for workshop same-day check and time-overlap checks
        byHari[slot.hari.nama]?.let { result.addAll(it) }
        // Same dosen on ANY day is irrelevant; only same-day matters for time overlap,
        // but dosen index lets us quickly find workshop same-hari for MK == mk check too.
        byDosen[mk.dosen.id]?.let { result.addAll(it) }
        byRuangan[ruangan.nama]?.let { result.addAll(it) }
        if (!mk.isWorkshop)
            bySemester[mk.semester]?.let { result.addAll(it) }
        return result
    }
}

class WelchPowellAlgorithm {
    private lateinit var daftarHari: List<Hari>
    private lateinit var daftarMataKuliah: List<MataKuliah>

    // ─── Slot generation with cache ──────────────────────────────────────────────

    // Cache: durasi → list of slots.  Invalidated when daftarHari changes.
    private val slotCache = HashMap<Int, List<Slot>>()

    private fun generateSlotsForDuration(durasi: Int): List<Slot> {
        if (durasi <= 0) return emptyList()
        return slotCache.getOrPut(durasi) {
            val slots = mutableListOf<Slot>()
            for (hariData in daftarHari) {
                val jamList = hariData.jamPelajaran // already sorted in Hari
                if (jamList.size < durasi) continue
                for (i in 0..jamList.size - durasi) {
                    val window = jamList.subList(i, i + durasi)
                    val isContiguous = window.zipWithNext().all { (a, b) -> b == a + 1 }
                    if (!isContiguous) continue
                    slots.add(Slot(hariData, window.first(), window.last() + 1))
                }
            }
            slots
        }
    }

    // ─── Conflict detection ──────────────────────────────────────────────────────

    private fun isTimeOverlap(slot1: Slot, slot2: Slot): Boolean {
        if (slot1.hari != slot2.hari) return false
        return slot1.jamMulai < slot2.jamSelesai && slot2.jamMulai < slot1.jamSelesai
    }

    private fun isConflict(
        mk1: MataKuliah, mk2: MataKuliah,
        slot1: Slot, slot2: Slot,
        ruangan1: Ruangan, ruangan2: Ruangan
    ): Boolean {
        // Workshop same-day check (before isTimeOverlap to avoid hari comparison twice)
        if (mk1 == mk2 && mk1.isWorkshop && slot1.hari == slot2.hari) return true

        val overlap = isTimeOverlap(slot1, slot2)
        if (!overlap) return false          // short-circuit — rest all need overlap

        if (mk1.dosen == mk2.dosen) return true
        if (ruangan1 == ruangan2) return true
        if (!mk1.isWorkshop && !mk2.isWorkshop && mk1.semester == mk2.semester) return true

        return false
    }

    private fun isRuanganCocok(mataKuliah: MataKuliah, ruangan: Ruangan): Boolean {
        val kebutuhan = if (mataKuliah.isWorkshop) TipePenggunaan.PRAKTIK else TipePenggunaan.TEORI
        return kebutuhan in ruangan.supports
    }

    // ─── Degree calculation (pre-computed once per MK) ───────────────────────────

    /**
     * Pre-compute degree for every MK in one O(n²) pass rather than once per
     * (MK, pertemuan) pair, which was O(n² × pertemuanPerMinggu).
     */
    private fun hitungSemuaDegree(daftarMK: List<MataKuliah>): Map<MataKuliah, Int> {
        val degrees = HashMap<MataKuliah, Int>(daftarMK.size * 2)
        for (mk in daftarMK) {
            var degree = 0
            for (other in daftarMK) {
                if (other == mk) continue
                if (other.dosen == mk.dosen) degree += 3 * other.pertemuanPerMinggu
                if (mk.isWorkshop && other.isWorkshop)
                    degree += 2 * (other.pertemuanPerMinggu + mk.pertemuanPerMinggu)
                if (!mk.isWorkshop && !other.isWorkshop && mk.semester == other.semester)
                    degree += 1
            }
            degrees[mk] = degree
        }
        return degrees
    }

    // ─── Slot priority ───────────────────────────────────────────────────────────

    private fun hitungPrioritasSlot(
        slot: Slot,
        jadwalPerHari: Map<Hari, List<JadwalItem>>, // pre-grouped outside the loop
        mataKuliah: MataKuliah,
        pertemuanKe: Int,
        pertemuan1Slot: Slot?                        // passed in to avoid re-scanning
    ): Int {
        var prioritas = 100
        val jadwalHariIni = jadwalPerHari[slot.hari] ?: emptyList()

        // 1. Distribution penalty
        prioritas -= jadwalHariIni.size * 3

        // 2. Workshop pertemuan-2 day bonus/penalty
        if (pertemuanKe == 2 && pertemuan1Slot != null) {
            if (pertemuan1Slot.hari == slot.hari) prioritas -= 50
            else prioritas += 10
        }

        // 3. Adjacency bonus
        for (existing in jadwalHariIni) {
            if (existing.slot.jamSelesai == slot.jamMulai) prioritas += 25
            if (slot.jamSelesai == existing.slot.jamMulai) prioritas += 25
        }

        // 4. Early start bonus (no existing sessions today)
        if (jadwalHariIni.isEmpty() && slot.jamMulai == slot.hari.jamPelajaran.first()) {
            prioritas += 20
        }

        // 5. Gap penalty — avoid full JadwalItem construction with a lightweight sort
        val allStarts = jadwalHariIni.map { it.slot.jamMulai to it.slot.jamSelesai }
            .plus(slot.jamMulai to slot.jamSelesai)
            .sortedBy { it.first }

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

        // 6. Time efficiency
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

    // ─── Main scheduling ─────────────────────────────────────────────────────────

    fun buatJadwal(
        daftarMataKuliah: List<MataKuliah>,
        daftarRuangan: List<Ruangan>,
        daftarHari: List<Hari>,
        overrideDurasiWorkshop: Int? = null
    ): Pair<Boolean, List<JadwalItem>> {
        this.daftarMataKuliah = daftarMataKuliah
        this.daftarHari = daftarHari
        slotCache.clear() // reset cache for fresh run

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

        // Pre-filter rooms per MK type to avoid repeated filtering in the hot loop
        val ruanganTeori = daftarRuangan.filter { TipePenggunaan.TEORI in it.supports }
            .sortedBy { if (it.supports.size == 1) 0 else 1 }
        val ruanganPraktik = daftarRuangan.filter { TipePenggunaan.PRAKTIK in it.supports }
            .sortedBy { if (it.supports.size == 1) 0 else 1 }

        val jadwal = mutableListOf<JadwalItem>()
        val conflictIndex = ConflictIndex()
        var isSuccess = true

        for ((mataKuliah, pertemuanKe, _) in expandedMK) {
            val durasi = if (mataKuliah.isWorkshop && overrideDurasiWorkshop != null)
                overrideDurasiWorkshop else mataKuliah.durasiJam

            val availableSlots = generateSlotsForDuration(durasi)
            if (availableSlots.isEmpty()) {
                println("⚠ Tidak ada slot tersedia untuk durasi ${mataKuliah.durasiJam} jam!")
                isSuccess = false
                continue
            }

            // Build per-hari view once per MK iteration (not per slot)
            val jadwalPerHari = jadwal.groupBy { it.slot.hari }

            // Find pertemuan-1 slot for workshop day-separation bonus
            val pertemuan1Slot = if (pertemuanKe == 2)
                jadwal.find { it.mataKuliah == mataKuliah && it.pertemuanKe == 1 }?.slot
            else null

            val slotsWithPriority = availableSlots
                .map { slot ->
                    SlotPriority(
                        slot,
                        hitungPrioritasSlot(
                            slot,
                            jadwalPerHari,
                            mataKuliah,
                            pertemuanKe,
                            pertemuan1Slot
                        )
                    )
                }
                .sortedByDescending { it.priority }

            val ruanganCocok = if (mataKuliah.isWorkshop) ruanganPraktik else ruanganTeori
            var berhasil = false

            outer@ for ((slot, _) in slotsWithPriority) {
                for (ruangan in ruanganCocok) {
                    // Use index for fast candidate lookup instead of scanning all jadwal
                    val adaKonflik = conflictIndex
                        .candidates(mataKuliah, slot, ruangan)
                        .any { item ->
                            isConflict(
                                mataKuliah,
                                item.mataKuliah,
                                slot,
                                item.slot,
                                ruangan,
                                item.ruangan
                            )
                        }

                    if (!adaKonflik) {
                        val newItem = JadwalItem(mataKuliah, ruangan, slot, pertemuanKe)
                        jadwal.add(newItem)
                        conflictIndex.add(newItem)
                        berhasil = true
                        val suffix =
                            if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                        println("✓ ${mataKuliah.nama}$suffix → ${slot.hari.nama} ${slot.jamMulai}:00-${slot.jamSelesai}:00 | ${ruangan.nama}")
                        break@outer
                    }
                }
            }

            if (!berhasil) {
                isSuccess = false
                val suffix =
                    if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                println("✗ GAGAL: ${mataKuliah.nama}$suffix tidak bisa dijadwalkan!")
            }
        }

        val jadwalFinal = jadwal.sortedWith(
            compareBy<JadwalItem> { item ->
                daftarHari.indexOfFirst { it.nama.equals(item.slot.hari.nama, ignoreCase = true) }
            }.thenBy { it.slot.jamMulai }
        )

        return Pair(isSuccess, jadwalFinal)
    }

    // ─── Display ─────────────────────────────────────────────────────────────────

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

    // ─── JSON conversion ─────────────────────────────────────────────────────────

    fun jadwalToJson(
        title: String,
        isSuccess: Boolean,
        jadwal: List<JadwalItem>,
        semester: String
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
                    ))
                )
            }

        val groupedByHari = jadwalData
            .groupBy { it.hari }
            .entries.sortedBy { (hari, _) -> HARI_ORDER[hari.lowercase()] ?: Int.MAX_VALUE }
            .map { (hari, items) -> JadwalPerItem(hari, items) }

        return Jadwal(title, isSuccess, groupedByRuangan, groupedByHari, semester)
    }

    companion object {
        // Moved out of jadwalToJson so it's allocated once, not on every call
        private val HARI_ORDER = mapOf(
            "senin" to 1, "selasa" to 2, "rabu" to 3,
            "kamis" to 4, "jumat" to 5, "sabtu" to 6, "minggu" to 7
        )
    }
}