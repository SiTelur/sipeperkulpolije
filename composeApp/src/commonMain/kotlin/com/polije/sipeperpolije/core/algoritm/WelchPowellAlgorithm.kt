package com.polije.sipeperpolije.core.algoritm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Dosen(val id: Int, val nama: String)

data class MataKuliah(
    val nama: String, val dosen: Dosen, val sks: Int,
    val isWorkshop: Boolean = false,
    val pertemuanPerMinggu: Int = if (isWorkshop) 2 else 1, // 4 jam = 2x pertemuan
    val durasiJam: Int = if (isWorkshop) 3 else 2,
    val durasiPerPertemuan: Int = durasiJam // Durasi tetap tidak dibagi
)

data class Slot(
    val hari: Hari,
    val jamMulai: Int = hari.jamPelajaran.first(),
    val jamSelesai: Int = hari.jamPelajaran.last()
) {
    override fun toString(): String = "${hari.nama} $jamMulai:00-$jamSelesai:00"
}

data class Ruangan(val nama: String, val canWorkshop: Boolean)

data class Hari(
    val nama: String,
    val jamPelajaran: List<Int>,
    val jamIstirahat: List<IntRange> = emptyList()
)

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
data class JadwalDataItem(val namaJadwal: String, val jam: String, val namaDosen: String)

@Serializable
data class Jadwal(
    @SerialName("is_success")
    val isSuccess: Boolean,
    @SerialName("jadwal")
    val listJadwal: Map<String, List<JadwalDataItem>>
)

class WelchPowellAlgorithm {
    private lateinit var daftarHari: List<Hari>
    private lateinit var daftarMataKuliah: List<MataKuliah>

//    // Generate slot waktu berdasarkan durasi mata kuliah yang dibutuhkan
//    private fun generateSlotsForDuration(durasi: Int): List<Slot> {
//        val slots = mutableListOf<Slot>()
////        for (hari in daftarHari) {
////            for (jamMulai in daftarHari) {
////                val jamSelesai = jamMulai + durasi
////
////                // Validasi berdasarkan hari
////                val isValidSlot = if (hari == "Jumat") {
////                    // Jumat: tidak boleh melewati jam 17, tidak boleh mulai di jam istirahat (11-13)
////                    jamSelesai <= 17 && jamMulai != 11 && jamMulai != 12 &&
////                            // Tidak boleh ada slot yang melewati jam istirahat 11-13
////                            !(jamMulai < 11 && jamSelesai > 11)
////                } else {
////                    // Hari lain: tidak boleh melewati jam 17, tidak boleh mulai di jam istirahat (12)
////                    jamSelesai <= 17 && jamMulai != 12 &&
////                            // Tidak boleh ada slot yang melewati jam istirahat 12-13
////                            !(jamMulai < 12 && jamSelesai > 12)
////                }
////
////                if (isValidSlot) {
////                    slots.add(Slot(hari, jamMulai, jamSelesai))
////                }
////            }
////        }
//
//        daftarHari.forEach { daftarHari ->
//            val hari =  daftarHari.nama
//            val jamAkhir = daftarHari.jamPelajaran.last() + 1
//                daftarHari.jamPelajaran.forEach { jamMulai ->
//                val jamSelesai = jamMulai + durasi
//
//                val isValidSlot = if (hari == "Jumat") {
//                    // Jumat: tidak boleh melewati jam 17, tidak boleh mulai di jam istirahat (11-13)
//                    jamSelesai <= jamAkhir
////                            && jamMulai != 11 && jamMulai != 12 &&
////                            // Tidak boleh ada slot yang melewati jam istirahat 11-13
////                            !(jamMulai < 11 && jamSelesai > 11)
//                } else {
//                    // Hari lain: tidak boleh melewati jam 17, tidak boleh mulai di jam istirahat (12)
//                    jamSelesai <= jamAkhir
////                            && jamMulai != 12 &&
////                            // Tidak boleh ada slot yang melewati jam istirahat 12-13
////                            !(jamMulai < 12 && jamSelesai > 12)
//                }
//
//                if (isValidSlot) {
//                    slots.add(Slot(hari, jamMulai, jamSelesai))
//                }
//            }
//
//
//        }
//        return slots
//    }

    private fun generateSlotsForDuration(durasi: Int): List<Slot> {
        val slots = mutableListOf<Slot>()

        daftarHari.forEach { hariData ->
            val hari = hariData.nama
            val jamList = hariData.jamPelajaran.sorted()
            val breakRanges = hariData.jamIstirahat

            for (i in 0..jamList.size - durasi) {
                val window = jamList.subList(i, i + durasi)

                // jam harus kontigu
                val isContiguous =
                    window.zipWithNext().all { (a, b) -> b == a + 1 }
                if (!isContiguous) continue

                val jamMulai = window.first()
                val jamSelesai = window.last() + 1

                // cek bentrok istirahat
                val overlapsBreak = breakRanges.any { br ->
                    jamMulai < br.last && jamSelesai > br.first
                }

                // ⬅️ INI YANG BENAR
                if (!overlapsBreak) {
                    slots.add(Slot(hariData, jamMulai, jamSelesai))
                }
            }
        }

        return slots
    }


    // Cek apakah dua mata kuliah berkonflik
    private fun isConflict(
        mk1: MataKuliah,
        mk2: MataKuliah,
        slot1: Slot,
        slot2: Slot,
        ruangan1: Ruangan,
        ruangan2: Ruangan
    ): Boolean {
        // Konflik dosen yang sama di waktu yang sama
        if (mk1.dosen == mk2.dosen && isTimeOverlap(slot1, slot2)) {
            return true
        }

        // Konflik ruangan yang sama di waktu yang sama
        if (ruangan1 == ruangan2 && isTimeOverlap(slot1, slot2)) {
            return true
        }

        return false
    }

    // Cek apakah dua slot waktu tumpang tindih
    private fun isTimeOverlap(slot1: Slot, slot2: Slot): Boolean {
        if (slot1.hari != slot2.hari) return false

        return !(slot1.jamSelesai <= slot2.jamMulai || slot2.jamSelesai <= slot1.jamMulai)
    }

    // Cek apakah ruangan cocok untuk mata kuliah
    private fun isRuanganCocok(mataKuliah: MataKuliah, ruangan: Ruangan): Boolean {
        // Workshop hanya bisa di Lab RSI atau Lab SKK
        if (mataKuliah.isWorkshop) {
            return ruangan.canWorkshop
        }

        // Mata kuliah biasa bisa di semua ruangan
        return true
    }

    // Hitung degree (jumlah konflik potensial) untuk setiap mata kuliah
    private fun hitungDegree(mataKuliah: MataKuliah, daftarMataKuliah: List<MataKuliah>): Int {
        var degree = 0
        for (mk in daftarMataKuliah) {
            if (mk != mataKuliah) {
                // Tambah degree jika dosen sama
                if (mk.dosen == mataKuliah.dosen) {
                    degree += 3 * mk.pertemuanPerMinggu // Konflik dosen lebih kritis
                }

                val isWorkshopCurrent =
                    mataKuliah.isWorkshop
                val isWorkshopOther =
                    mk.isWorkshop

                if (isWorkshopCurrent && isWorkshopOther) {
                    degree += 2 * (mk.pertemuanPerMinggu + mataKuliah.pertemuanPerMinggu)
                }
            }
        }
        return degree
    }

    // Hitung prioritas slot berdasarkan minimisasi celah waktu
    private fun hitungPrioritasSlot(
        slot: Slot,
        jadwal: List<JadwalItem>,
        mataKuliah: MataKuliah,
        pertemuanKe: Int
    ): Int {
        var prioritas = 100

        // 1. Distribusi merata per hari (kurangi bobot ini)
        val jadwalHariIni = jadwal.count { it.slot.hari == slot.hari }
        prioritas -= jadwalHariIni * 3

        // 2. Untuk pertemuan ke-2, hindari hari yang sama dengan pertemuan ke-1
        if (pertemuanKe == 2) {
            val pertemuan1 = jadwal.find {
                it.mataKuliah == mataKuliah && it.pertemuanKe == 1
            }
            if (pertemuan1 != null && pertemuan1.slot.hari == slot.hari) {
                prioritas -= 20
            }
        }

        // 3. PRIORITAS TINGGI: Minimisasi celah waktu
        val jadwalHariIni2 =
            jadwal.filter { it.slot.hari == slot.hari }.sortedBy { it.slot.jamMulai }

        // Bonus besar jika slot ini bisa mengisi celah atau menempel dengan slot existing
        var adjacencyBonus = 0
        for (existingSlot in jadwalHariIni2) {
            // Bonus jika slot ini langsung setelah slot yang ada (tanpa celah)
            if (existingSlot.slot.jamSelesai == slot.jamMulai) {
                adjacencyBonus += 25
            }
            // Bonus jika slot ini langsung sebelum slot yang ada (tanpa celah)
            if (slot.jamSelesai == existingSlot.slot.jamMulai) {
                adjacencyBonus += 25
            }
        }
        prioritas += adjacencyBonus

        // 4. Bonus untuk menggunakan waktu dari jam awal secara berurutan
        val jamAwal = slot.hari.jamPelajaran.first()
        if (slot.jamMulai == jamAwal) {
            prioritas += 20 // Prioritas tinggi untuk mulai jam awal
        } else {
            // Cek apakah ada slot yang berakhir tepat sebelum slot ini dimulai
            val adaSlotSebelum = jadwalHariIni2.any { it.slot.jamSelesai == slot.jamMulai }
            if (adaSlotSebelum) {
                prioritas += 15 // Bonus untuk continuity
            }
        }

        // 5. Penalti untuk membuat celah
        // Hitung berapa celah yang akan terbentuk jika slot ini digunakan
        val simulatedSchedule = jadwalHariIni2.toMutableList()
        simulatedSchedule.add(JadwalItem(mataKuliah, Ruangan(nama = "", true), slot, pertemuanKe))
        val sortedSimulated = simulatedSchedule.sortedBy { it.slot.jamMulai }

        var gapPenalty = 0
        for (i in 0..<sortedSimulated.lastIndex) {
            val currentEnd = sortedSimulated[i].slot.jamSelesai
            val nextStart = sortedSimulated[i + 1].slot.jamMulai

            // Gap mentah
            val rawGap = (nextStart - currentEnd).coerceAtLeast(0)

            // Potong jam istirahat
            val breakCut = slot.hari.jamIstirahat.sumOf { breakRange ->
                (maxOf(currentEnd, breakRange.first)..<minOf(nextStart, breakRange.last + 1))
                    .count()
            }

            val actualGap = (rawGap - breakCut).coerceAtLeast(0)

            if (actualGap > 0) {
                gapPenalty += actualGap * 5
            }
        }

        prioritas -= gapPenalty

        // 6. Bonus untuk slot yang menggunakan waktu paling efisien
        // Hitung total waktu yang akan terpakai vs waktu kosong
        if (jadwalHariIni2.isNotEmpty()) {
            val minStart = minOf(slot.jamMulai, jadwalHariIni2.minOf { it.slot.jamMulai })
            val maxEnd = maxOf(slot.jamSelesai, jadwalHariIni2.maxOf { it.slot.jamSelesai })
            val totalTimeSpan = maxEnd - minStart
            val totalUsedTime =
                slot.jamSelesai - slot.jamMulai + jadwalHariIni2.sumOf { it.slot.jamSelesai - it.slot.jamMulai }
            val efficiency = (totalUsedTime.toDouble() / totalTimeSpan * 100).toInt()
            prioritas += efficiency / 10 // Bonus berdasarkan efisiensi
        }

        // 7. Penalti untuk melewati jam istirahat
        val breakRanges = slot.hari.jamIstirahat

        for (breakRange in breakRanges) {
            val overlap =
                slot.jamMulai < breakRange.last + 1 &&
                        slot.jamSelesai > breakRange.first

            if (overlap) {
                prioritas -= 10
            }
        }

        return prioritas
    }

    // Implementasi algoritma Welch-Powell yang diperbaiki
    fun buatJadwal(
        daftarMataKuliah: List<MataKuliah>,
        daftarRuangan: List<Ruangan>,
        daftarHari: List<Hari>
    ): Pair<Boolean, List<JadwalItem>> {
        this.daftarMataKuliah = daftarMataKuliah
        this.daftarHari = daftarHari

        // Expand mata kuliah yang memiliki multiple pertemuan
        val expandedMataKuliah = mutableListOf<Pair<MataKuliah, Int>>()
        for (mk in daftarMataKuliah) {
            for (pertemuan in 1..mk.pertemuanPerMinggu) {
                expandedMataKuliah.add(Pair(mk, pertemuan))
            }
        }

        // Step 1: Hitung degree dan urutkan mata kuliah (descending)
        val mataKuliahDenganDegree = expandedMataKuliah.map { (mk, pertemuan) ->
            Triple(mk, pertemuan, hitungDegree(mk, daftarMataKuliah))
        }.sortedByDescending { it.third }

        println("Urutan mata kuliah berdasarkan degree:")
        mataKuliahDenganDegree.forEach { (mk, pertemuan, degree) ->
            val suffix = if (mk.pertemuanPerMinggu > 1) " (Pertemuan $pertemuan)" else ""
            println("${mk.nama}$suffix - Degree: $degree")
        }
        println()

        val jadwal = mutableListOf<JadwalItem>()
        var isSuccess: Boolean = true

        // Step 2: Assign slot untuk setiap mata kuliah
        for ((mataKuliah, pertemuanKe, _) in mataKuliahDenganDegree) {
            var berhasilDijadwalkan = false

            // Generate slot berdasarkan durasi per pertemuan yang spesifik
            val availableSlots = generateSlotsForDuration(mataKuliah.durasiPerPertemuan)

            // Hitung prioritas untuk setiap slot
            val slotsWithPriority = availableSlots.map { slot ->
                val prioritas = hitungPrioritasSlot(slot, jadwal, mataKuliah, pertemuanKe)
                Pair(slot, prioritas)
            }.sortedByDescending { it.second }

            // Coba setiap slot dengan prioritas tertinggi
            for ((slot, prioritas) in slotsWithPriority) {
                val ruanganCocok = daftarRuangan.filter { isRuanganCocok(mataKuliah, it) }

                for (ruangan in ruanganCocok) {
                    // Cek konflik dengan jadwal yang sudah ada
                    var adaKonflik = false
                    for (jadwalItem in jadwal) {
                        if (isConflict(
                                mataKuliah,
                                jadwalItem.mataKuliah,
                                slot,
                                jadwalItem.slot,
                                ruangan,
                                jadwalItem.ruangan
                            )
                        ) {
                            adaKonflik = true
                            break
                        }
                    }

                    if (!adaKonflik) {
                        jadwal.add(JadwalItem(mataKuliah, ruangan, slot, pertemuanKe))
                        berhasilDijadwalkan = true
                        val suffix =
                            if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                        println("✓ ${mataKuliah.nama}$suffix dijadwalkan: ${slot.hari} ${slot.jamMulai}:00-${slot.jamSelesai}:00 di $ruangan (Prioritas: $prioritas)")
                        break
                    }
                }

                if (berhasilDijadwalkan) break
            }

            if (!berhasilDijadwalkan) {
                isSuccess = false
                val suffix =
                    if (mataKuliah.pertemuanPerMinggu > 1) " (Pertemuan $pertemuanKe)" else ""
                println("⚠ PERINGATAN: ${mataKuliah.nama}$suffix tidak bisa dijadwalkan!")
            }
        }

        val jadwalFinal = jadwal.sortedWith(compareBy<JadwalItem> {
            daftarHari.indexOfFirst { map ->
                map.nama.contains(
                    it.slot.hari.nama,
                    ignoreCase = true
                )
            }
        }.thenBy { it.slot.jamMulai })
        return Pair(isSuccess, jadwalFinal.toList())
    }

    // Tampilkan hasil jadwal dengan analisis distribusi
    fun tampilkanJadwal(jadwal: List<JadwalItem>, daftarRuangan: List<Ruangan>) {
        println("=".repeat(80))
        println("JADWAL MATA KULIAH")
        println("=".repeat(80))
        println("📝 Keterangan Jam Istirahat:")
        println("   • Senin-Kamis: Istirahat jam 12:00-13:00")
        println("   • Jumat: Istirahat jam 11:00-13:00")
        println("=".repeat(80))

        for (hari in daftarHari) {
            val hariString = hari.nama
            val jadwalHari = jadwal.filter { it.slot.hari.nama == hariString }
            val jamIstirahat = if (hariString == "Jumat") "11:00-13:00" else "12:00-13:00"

            println("\n$hariString (${jadwalHari.size} sesi) - Istirahat: $jamIstirahat:")
            println("-".repeat(40))
            if (jadwalHari.isNotEmpty()) {
                jadwalHari.sortedBy { it.slot.jamMulai }.forEach { item ->
                    println(
                        "${item.slot.jamMulai}:00-${item.slot.jamSelesai}:00 | " +
                                "${item.ruangan.nama.padEnd(12)} | " +
                                "${item.getNamaLengkap().padEnd(30)} | " +
                                "${item.mataKuliah.dosen.nama} (${item.mataKuliah.sks} SKS)"
                    )
                }

                // Tampilkan analisis celah waktu
                println("📊 Analisis penggunaan waktu:")
                analyzeTimeGaps(jadwalHari, hari)
            } else {
                println("Tidak ada mata kuliah terjadwal")
            }
        }

        println("\n" + "=".repeat(80))
        println("STATISTIK PENJADWALAN")
        println("=".repeat(80))

        // Hitung total sesi dan mata kuliah unik
        val totalSesi = jadwal.size
        val mataKuliahUnik = jadwal.map { it.mataKuliah.nama }.toSet().size
        val totalMataKuliah = daftarMataKuliah.size

        println("Total sesi terjadwal: $totalSesi")
        println("Mata kuliah unik terjadwal: $mataKuliahUnik dari $totalMataKuliah")

        // Distribusi per hari
        println("\nDistribusi Sesi per Hari:")
        daftarHari.forEach { hari ->
            val hari = hari.nama
            val count = jadwal.count { it.slot.hari.nama == hari }
            println("$hari: $count sesi")
        }

        // Statistik mata kuliah 2x seminggu
        val mk2xSeminggu = jadwal.filter { it.mataKuliah.pertemuanPerMinggu == 2 }
        if (mk2xSeminggu.isNotEmpty()) {
            println("\nMata Kuliah 2x Seminggu:")
            mk2xSeminggu.groupBy { it.mataKuliah.nama }.forEach { (nama, sesi) ->
                if (sesi.size == 2) {
                    val hari1 = sesi[0].slot.hari
                    val hari2 = sesi[1].slot.hari
                    println("$nama: $hari1 & $hari2")
                } else {
                    println("$nama: ${sesi.size}/2 sesi terjadwal ⚠")
                }
            }
        }

        // Statistik ruangan
        println("\nPenggunaan Ruangan:")
        daftarRuangan.forEach { ruangan ->
            val count = jadwal.count { it.ruangan == ruangan }
            println("$ruangan: $count sesi")
        }

        // Statistik dosen
        println("\nBeban Mengajar Dosen:")
        jadwal.groupBy { it.mataKuliah.dosen.nama }.forEach { (dosen, items) ->
            val totalSKS = items.map { it.mataKuliah }.toSet().sumOf { it.sks }
            val totalSesi = items.size
            println("$dosen: $totalSesi sesi, $totalSKS SKS")
        }

        // Cek mata kuliah yang belum terjadwal lengkap
        println("\n⚠ Status Penjadwalan per Mata Kuliah:")
        daftarMataKuliah.forEach { mk ->
            val sesiTerjadwal = jadwal.count { it.mataKuliah == mk }
            val sesiDibutuhkan = mk.pertemuanPerMinggu

            when {
                sesiTerjadwal == sesiDibutuhkan -> println("✓ ${mk.nama}: Lengkap ($sesiTerjadwal/$sesiDibutuhkan)")
                sesiTerjadwal > 0 -> println("⚠ ${mk.nama}: Tidak lengkap ($sesiTerjadwal/$sesiDibutuhkan)")
                else -> println("✗ ${mk.nama}: Belum terjadwal ($sesiTerjadwal/$sesiDibutuhkan)")
            }
        }
    }

    // Fungsi baru untuk menganalisis celah waktu dan memberikan saran
    private fun analyzeTimeGaps(
        jadwalHari: List<JadwalItem>,
        hari: Hari
    ) {
        if (jadwalHari.isEmpty()) {
            println("   📭 Tidak ada jadwal")
            return
        }

        val sortedJadwal = jadwalHari.sortedBy { it.slot.jamMulai }
        val gaps = mutableListOf<Triple<Int, Int, Int>>() // start, end, duration

        val jamMulaiHari = hari.jamPelajaran.first()
        val jamAkhirHari = 17

        // Break opsional

        val breakRange: IntRange? = hari.jamIstirahat.firstOrNull()

        fun addGapConsideringBreak(start: Int, end: Int) {
            if (start >= end) return

            if (breakRange != null &&
                start < breakRange.last &&
                end > breakRange.first
            ) {
                // gap sebelum break
                if (start < breakRange.first) {
                    gaps.add(
                        Triple(start, breakRange.first, breakRange.first - start)
                    )
                }
                // gap setelah break
                if (end > breakRange.last) {
                    gaps.add(
                        Triple(breakRange.last, end, end - breakRange.last)
                    )
                }
            } else {
                gaps.add(Triple(start, end, end - start))
            }
        }

        // Gap dari awal hari
        addGapConsideringBreak(
            jamMulaiHari,
            sortedJadwal.first().slot.jamMulai
        )

        // Gap antar slot
        for (i in 0..<sortedJadwal.size - 1) {
            val currentEnd = sortedJadwal[i].slot.jamSelesai
            val nextStart = sortedJadwal[i + 1].slot.jamMulai
            addGapConsideringBreak(currentEnd, nextStart)
        }

        // Gap sampai akhir hari
        addGapConsideringBreak(
            sortedJadwal.last().slot.jamSelesai,
            jamAkhirHari
        )

        // ===== OUTPUT GAP =====
        if (gaps.isNotEmpty()) {
            val totalGapTime = gaps.sumOf { it.third }
            println("   ⚠️ Total celah waktu: $totalGapTime jam")

            gaps.forEach { (start, end, duration) ->
                when {
                    duration >= 4 ->
                        println("   🔴 Celah besar: $start:00-$end:00 ($duration jam)")

                    duration >= 2 ->
                        println("   🟡 Celah sedang: $start:00-$end:00 ($duration jam)")

                    else ->
                        println("   🟢 Celah kecil: $start:00-$end:00 ($duration jam)")
                }
            }

            if (totalGapTime >= 4) {
                println("   💡 Saran: Pertimbangkan penjadwalan yang lebih kompak")
            }
        } else {
            println("   ✅ Tidak ada celah waktu - Optimal!")
        }

        // ===== EFISIENSI =====
        val firstStart = sortedJadwal.first().slot.jamMulai
        val lastEnd = sortedJadwal.last().slot.jamSelesai

        val usedTime = sortedJadwal.sumOf {
            it.slot.jamSelesai - it.slot.jamMulai
        }

        val totalSpan = lastEnd - firstStart

        val breakTimeInSpan =
            if (breakRange != null &&
                firstStart < breakRange.last &&
                lastEnd > breakRange.first
            ) {
                minOf(lastEnd, breakRange.last) -
                        maxOf(firstStart, breakRange.first)
            } else 0

        val effectiveSpan = totalSpan - breakTimeInSpan

        val efficiency =
            if (effectiveSpan > 0)
                (usedTime.toDouble() / effectiveSpan * 100).toInt()
            else 100

        println(
            "   📊 Efisiensi waktu: $usedTime/$effectiveSpan jam ($efficiency%)" +
                    (breakRange?.let {
                        " [Istirahat: ${it.first}:00-${it.last}:00]"
                    } ?: " [Tanpa istirahat]")
        )
    }


    fun jadwalToJson(isSuccess: Boolean, jadwal: List<JadwalItem>): Jadwal {
        val groupedJadwal: Map<String, List<JadwalDataItem>> =
            jadwal
                .groupBy { it.ruangan.nama } // Aula, RSI, dll
                .entries
                .sortedBy { it.key }
                .associate { it.key to it.value }
                .mapValues { (_, jadwalRuangan) ->
                    jadwalRuangan.map { value ->
                        JadwalDataItem(
                            value.mataKuliah.nama,
                            jam = "${value.slot.jamMulai}-${value.slot.jamSelesai}",
                            namaDosen = value.mataKuliah.dosen.nama
                        )
                    }
                }

        return Jadwal(isSuccess, groupedJadwal)
    }
}