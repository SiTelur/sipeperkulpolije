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

    var durasiJam: Int = 0
        get() {
            val teori = sksTeori * DurasiConfig.jamPerSksTeori
            val praktik =
                (if (sksPraktek > 2) sksPraktek / 2 else sksPraktek) * DurasiConfig.jamPerSksPraktik
            return teori + praktik
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
    val pertemuanKe: Int = 1 // Pertemuan ke berapa (1 atau 2 untuk mata kuliah 2x seminggu)
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
    val semester: Int, val sks: Int, val namaRuangan: String
)

@Serializable
data class Jadwal(
    @SerialName("is_success")
    val isSuccess: Boolean,
    @SerialName("jadwal")
    val listJadwal: List<JadwalPerItem>,
    @SerialName("jadwal_view")
    val listJadwalView: List<JadwalPerItem>,
    val semester: String
)

@Serializable
data class JadwalPerItem(val nama: String, val items: List<JadwalDataItem>)
data class MKDegree(
    val mk: MataKuliah,
    val pertemuan: Int,
    val degree: Int
)

data class SlotPriority(
    val slot: Slot,
    val priority: Int
)


class WelchPowellAlgorithm {
    private lateinit var daftarHari: List<Hari>
    private lateinit var daftarMataKuliah: List<MataKuliah>

    // ─── Slot generation ────────────────────────────────────────────────────────

    /**
     * Menghasilkan semua slot kontigu dengan panjang [durasi] jam pelajaran.
     * Slot tidak boleh memotong jam istirahat karena jamPelajaran sudah mengecualikannya.
     * Contiguity diperiksa pada jam riil (bukan indeks) sehingga lompatan di sekitar
     * istirahat tidak dianggap kontigu.
     */
    private fun generateSlotsForDuration(durasi: Int): List<Slot> {
        if (durasi <= 0) return emptyList()
        val slots = mutableListOf<Slot>()

        for (hariData in daftarHari) {
            val jamList = hariData.jamPelajaran.sorted()
            if (jamList.size < durasi) continue

            for (i in 0..jamList.size - durasi) {
                val window = jamList.subList(i, i + durasi)

                // Semua jam dalam window harus kontigu (tidak ada lompatan)
                val isContiguous = window.zipWithNext().all { (a, b) -> b == a + 1 }
                if (!isContiguous) continue

                slots.add(Slot(hariData, window.first(), window.last() + 1))
            }
        }
        return slots
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
        val overlap = isTimeOverlap(slot1, slot2)

        // Workshop tidak boleh di hari yang sama untuk pertemuan berbeda dari MK yang sama
        if (mk1 == mk2 && mk1.isWorkshop && slot1.hari == slot2.hari) return true

        // Dosen yang sama tidak boleh mengajar bersamaan
        if (mk1.dosen == mk2.dosen && overlap) return true

        // Ruangan yang sama tidak boleh dipakai bersamaan
        if (ruangan1 == ruangan2 && overlap) return true

        // Mata kuliah non-workshop text sama tidak boleh paralel
        if (!mk1.isWorkshop && !mk2.isWorkshop &&
            mk1.semester == mk2.semester && overlap
        ) return true

        return false
    }

    private fun isRuanganCocok(mataKuliah: MataKuliah, ruangan: Ruangan): Boolean {
        val kebutuhan = if (mataKuliah.isWorkshop) TipePenggunaan.PRAKTIK else TipePenggunaan.TEORI
        return kebutuhan in ruangan.supports
    }

    // ─── Degree calculation ──────────────────────────────────────────────────────

    /**
     * Degree = estimasi jumlah konflik potensial sebuah MK dengan MK lain.
     * Bobot lebih besar untuk konflik dosen (lebih kritis) dan workshop.
     */
    private fun hitungDegree(mataKuliah: MataKuliah, daftarMK: List<MataKuliah>): Int {
        var degree = 0
        for (mk in daftarMK) {
            if (mk == mataKuliah) continue

            if (mk.dosen == mataKuliah.dosen)
                degree += 3 * mk.pertemuanPerMinggu

            if (mataKuliah.isWorkshop && mk.isWorkshop)
                degree += 2 * (mk.pertemuanPerMinggu + mataKuliah.pertemuanPerMinggu)

            // Semester sama → potensi konflik paralel
            if (!mataKuliah.isWorkshop && !mk.isWorkshop &&
                mataKuliah.semester == mk.semester
            ) degree += 1
        }
        return degree
    }

    // ─── Slot priority ───────────────────────────────────────────────────────────

    /**
     * Menghitung skor prioritas slot.
     * Skor lebih tinggi = lebih disukai.
     * Tujuan: jadwal kompak (minimal celah), distribusi merata antar hari,
     * dan pertemuan ke-2 workshop di hari berbeda.
     */
    private fun hitungPrioritasSlot(
        slot: Slot,
        jadwal: List<JadwalItem>,
        mataKuliah: MataKuliah,
        pertemuanKe: Int
    ): Int {
        var prioritas = 100

        val jadwalPerHari = jadwal.groupBy { it.slot.hari }
        val jadwalHariIni = jadwalPerHari[slot.hari] ?: emptyList()

        // 1. Distribusi merata — penalti ringan jika hari ini sudah padat
        prioritas -= jadwalHariIni.size * 3

        // 2. Pertemuan ke-2 workshop: bonus besar jika beda hari dari pertemuan ke-1
        if (pertemuanKe == 2) {
            val pertemuan1 = jadwal.find { it.mataKuliah == mataKuliah && it.pertemuanKe == 1 }
            if (pertemuan1 != null) {
                if (pertemuan1.slot.hari == slot.hari) prioritas -= 50  // harus beda hari untuk workshop
                else prioritas += 10
            }
        }

        // 3. Adjacency bonus — slot menempel langsung dengan slot yang ada (tanpa celah)
        for (existing in jadwalHariIni) {
            if (existing.slot.jamSelesai == slot.jamMulai) prioritas += 25
            if (slot.jamSelesai == existing.slot.jamMulai) prioritas += 25
        }

        // 4. Mulai dari jam paling awal hari ini (jika belum ada jadwal)
        if (jadwalHariIni.isEmpty() && slot.jamMulai == slot.hari.jamPelajaran.first()) {
            prioritas += 20
        }

        // 5. Gap penalty — hitung celah yang akan terbentuk setelah slot ini ditambahkan
        val simulasi = (jadwalHariIni + listOf(
            JadwalItem(
                mataKuliah,
                Ruangan("_", setOf(TipePenggunaan.TEORI, TipePenggunaan.PRAKTIK)),
                slot,
                pertemuanKe
            )
        )).sortedBy { it.slot.jamMulai }

        var gapPenalty = 0
        for (i in 0 until simulasi.lastIndex) {
            val end = simulasi[i].slot.jamSelesai
            val start = simulasi[i + 1].slot.jamMulai
            if (start > end) {
                // Kurangi durasi istirahat yang ada di dalam celah
                val breakInGap = if (slot.hari.jamIstirahat != IntRange.EMPTY) {
                    (maxOf(end, slot.hari.jamIstirahat.first) until
                            minOf(start, slot.hari.jamIstirahat.last + 1)).count()
                } else 0
                val actualGap = (start - end - breakInGap).coerceAtLeast(0)
                gapPenalty += actualGap * 8
            }
        }
        prioritas -= gapPenalty

        // 6. Efisiensi waktu hari ini
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

        // Expand MK dengan multiple pertemuan
        val expandedMK = daftarMataKuliah.flatMap { mk ->
            (1..mk.pertemuanPerMinggu).map { Pair(mk, it) }
        }

        // Urutkan berdasarkan degree (descending), lalu durasi (descending)
        val mkDenganDegree = expandedMK.map { (mk, pertemuan) ->
            MKDegree(mk, pertemuan, hitungDegree(mk, daftarMataKuliah))
        }.sortedWith(
            compareByDescending<MKDegree> { it.degree }
                .thenByDescending { it.mk.durasiJam }
                .thenByDescending { it.mk.isWorkshop }
        )

        println("Urutan mata kuliah berdasarkan degree:")
        mkDenganDegree.forEach { (mk, pertemuan, degree) ->
            val suffix = if (mk.pertemuanPerMinggu > 1) " (Pertemuan $pertemuan)" else ""
            println("  ${mk.nama}$suffix — Degree: $degree, Durasi: ${mk.durasiJam} jam")
        }
        println()

        val jadwal = mutableListOf<JadwalItem>()
        var isSuccess = true

        for ((mataKuliah, pertemuanKe, _) in mkDenganDegree) {
            val durasi = if (mataKuliah.isWorkshop && overrideDurasiWorkshop != null)
                overrideDurasiWorkshop
            else
                mataKuliah.durasiJam

            val availableSlots = generateSlotsForDuration(durasi)

            if (availableSlots.isEmpty()) {
                println("⚠ Tidak ada slot tersedia untuk durasi ${mataKuliah.durasiJam} jam!")
                isSuccess = false
                continue
            }

            val slotsWithPriority = availableSlots
                .map { slot ->
                    SlotPriority(slot, hitungPrioritasSlot(slot, jadwal, mataKuliah, pertemuanKe))
                }
                .sortedByDescending { it.priority }

            // Ruangan yang cocok, diurutkan: khusus (supports.size==1) dulu
            val ruanganCocok = daftarRuangan
                .filter { isRuanganCocok(mataKuliah, it) }
                .sortedBy { if (it.supports.size == 1) 0 else 1 }

            var berhasil = false

            outer@ for ((slot, _) in slotsWithPriority) {
                for (ruangan in ruanganCocok) {
                    val adaKonflik = jadwal.any { item ->
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
                        jadwal.add(JadwalItem(mataKuliah, ruangan, slot, pertemuanKe))
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
            val c = jadwal.count { it.slot.hari.nama == hari.nama }
            println("  ${hari.nama}: $c sesi")
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
            val dibutuhkan = mk.pertemuanPerMinggu
            val status = when {
                terjadwal == dibutuhkan -> "✓"
                terjadwal > 0 -> "⚠ tidak lengkap"
                else -> "✗ belum terjadwal"
            }
            println("  $status ${mk.nama}: $terjadwal/$dibutuhkan")
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
        isSuccess: Boolean,
        jadwal: List<JadwalItem>,
        semester: String
    ): Jadwal {
        val hariOrder = mapOf(
            "senin" to 1, "selasa" to 2, "rabu" to 3,
            "kamis" to 4, "jumat" to 5, "sabtu" to 6, "minggu" to 7
        )

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
            .entries
            .sortedBy { it.key }
            .map { (ruangan, items) ->

                val sortedItems = items.sortedWith(
                    compareBy(
                        { hariOrder[it.hari.lowercase()] ?: Int.MAX_VALUE },
                        { it.jamMulai }
                    )
                )

                JadwalPerItem(
                    nama = ruangan,
                    items = sortedItems
                )
            }

        val groupedByHari = jadwalData
            .groupBy { it.hari }
            .entries
            .sortedBy { (hari, _) -> hariOrder[hari.lowercase()] ?: Int.MAX_VALUE }
            .map { (hari, items) -> JadwalPerItem(hari, items) }

        return Jadwal(
            isSuccess = isSuccess,
            listJadwal = groupedByRuangan,
            listJadwalView = groupedByHari,
            semester = semester
        )
    }
}