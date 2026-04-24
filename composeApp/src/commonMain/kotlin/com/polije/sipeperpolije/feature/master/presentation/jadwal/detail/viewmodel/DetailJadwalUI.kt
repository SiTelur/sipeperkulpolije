package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel

data class DetailJadwalUI(
    val isSuccess: Boolean = false,
    val jadwal: List<DetailJadwalListUI> = emptyList(),
    val jadwalView: List<DetailJadwalListUI> = emptyList(),
    val unscheduledCount: Int = 0,
    val unscheduledItems: List<UnscheduledItemUI> = emptyList(),
    val summary: List<DosenSummaryUI> = emptyList(),
    val teknisiSummary: List<TeknisiSummaryUI> = emptyList()
)

data class DosenSummaryUI(
    val namaDosen: String,
    val totalSesi: Int,
    val sksTeori: Int,
    val sksWorkshop: Int,
    val sksAjar: Int,
    val bebanSks: Int,
)

data class DetailJadwalListUI(
    val nama: String,
    val item: List<DetailJadwalItemUI>
)

data class UnscheduledItemUI(
    val namaMk: String,
    val semester: Int,
    val sks: Int,
    val namaDosen: String,
    val pertemuanKe: Int,
    val alasan: String = "Tidak ada slot/ruangan yang tersedia",
    val degree: Int
)

data class DetailJadwalItemUI(
    val jam: String,
    val namaDosen: String,
    val namaJadwal: String,
    val namaRuangan: String,
    val sks: Int,
    val semester: Int, val namaTeknisi: String? = null
)

data class TeknisiSummaryUI(
    val namaTeknisi: String,
    val totalSesi: Int,
    val bebanSks: Int
)
