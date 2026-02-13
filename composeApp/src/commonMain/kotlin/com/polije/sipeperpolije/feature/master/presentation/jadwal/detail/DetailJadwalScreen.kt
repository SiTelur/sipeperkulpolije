package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.polije.sipeperpolije.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class Jadwal(
    val jam: String,
    val sks: String,
    val mataKuliah: String,
    val dosen: String,
    val ruangan: String,
    val semester: Int
)

data class JadwalHarian(
    val hari: String,
    val jadwal: List<Jadwal>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailJadwalScreen() {
    val jadwalHarian = listOf(
        JadwalHarian(
            "Senin", listOf(
                Jadwal(
                    "08.00 - 10.30",
                    "3 SKS",
                    "Pengantar Teknologi Informasi",
                    "Dr. Budi Santoso, M.Kom",
                    "Ruang Kelas 101",
                    1
                ),
                Jadwal(
                    "10.30 - 13.00",
                    "4 SKS",
                    "Struktur Data & Algoritma",
                    "Siti Aminah, S.T., M.T.",
                    "Lab RSI",
                    3
                ),
                Jadwal(
                    "13.30 - 16.00",
                    "3 SKS",
                    "Pengembangan Aplikasi Mobile",
                    "Andi Pratama, M.Cs",
                    "Lab Mobile",
                    5
                )
            )
        ),
        JadwalHarian(
            "Selasa", listOf(
                Jadwal(
                    "08.00 - 10.30",
                    "3 SKS",
                    "Basis Data Lanjut",
                    "Rina Wulandari, M.Kom",
                    "Lab SKK",
                    3
                ),
                Jadwal(
                    "10.30 - 13.00",
                    "3 SKS",
                    "Kecerdasan Buatan",
                    "Prof. Dr. Bambang",
                    "Kelas 305",
                    5
                )
            )
        ),
        JadwalHarian(
            "Rabu",
            listOf(
                Jadwal(
                    "08.00 - 09.40",
                    "2 SKS",
                    "Bahasa Inggris I",
                    "Sarah Jones, M.Ed",
                    "Lab Bahasa",
                    1
                )
            )
        ),
        JadwalHarian("Kamis", emptyList()),
        JadwalHarian(
            "Jumat",
            listOf(
                Jadwal(
                    "08.00 - 11.20",
                    "4 SKS",
                    "Proyek Perangkat Lunak",
                    "Team Teaching",
                    "Lab Proyek",
                    5
                )
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Akademik", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Jadwal Kegiatan Perkuliahan dan Praktikum Semester Ganjil Tahun Akademik 2025-2026",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LegendChip(semester = 1)
                        LegendChip(semester = 3)
                        LegendChip(semester = 5)
                    }
                }
            }

            items(jadwalHarian) { harian ->
                DaySchedule(harian)
            }
        }
    }
}

@Composable
fun LegendChip(semester: Int) {
    val color = when (semester) {
        1 -> Color(0xFFFACC15)
        3 -> Color(0xFF10B981)
        5 -> Color(0xFF3B82F6)
        else -> MaterialTheme.colorScheme.secondary
    }
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Semester $semester",
            color = color.copy(alpha = 0.9f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun DaySchedule(jadwalHarian: JadwalHarian) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(jadwalHarian.hari, fontWeight = FontWeight.Bold)
            Text(
                "${jadwalHarian.jadwal.size} Mata Kuliah",
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (jadwalHarian.jadwal.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada jadwal kuliah", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                jadwalHarian.jadwal.forEach {
                    JadwalItem(it)
                }
            }
        }
    }
}

@Composable
fun JadwalItem(jadwal: Jadwal) {
    val semesterColor = when (jadwal.semester) {
        1 -> Color(0xFFFACC15)
        3 -> Color(0xFF10B981)
        5 -> Color(0xFF3B82F6)
        else -> MaterialTheme.colorScheme.secondary
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(140.dp)
                    .background(
                        semesterColor,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = jadwal.jam,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                semesterColor.copy(alpha = 0.1f),
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(jadwal.sks, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = jadwal.mataKuliah,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Dosen",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(jadwal.dosen, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "Ruangan",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(jadwal.ruangan, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview
@Composable
fun DetailJadwalPreview() {
    AppTheme {
        DetailJadwalScreen()
    }
}
