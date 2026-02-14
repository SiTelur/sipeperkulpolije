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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalAction
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalEvent
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalItemUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalListUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalViewModel
import com.polije.sipeperpolije.utils.ObserveAsEvent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailJadwalScreen(
    id: Int,
    detailJadwalViewModel: DetailJadwalViewModel = koinViewModel()
) {

    val state by detailJadwalViewModel.state.collectAsStateWithLifecycle()

    val snackbarHost = LocalSnackbarHostState.current

    LaunchedEffect(id) {
        detailJadwalViewModel.onAction(DetailJadwalAction.OnDetailInitial(id))
    }

    ObserveAsEvent(detailJadwalViewModel.channel) { event ->
        when (event) {
            is DetailJadwalEvent.OnFailure -> {
                snackbarHost.showSnackbar(event.message)
            }

            DetailJadwalEvent.OnSuccess -> {
                snackbarHost.showSnackbar("Berhasil memuat jadwal")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Jadwal Akademik", fontWeight = FontWeight.SemiBold) },
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LegendChip(semester = 1)
                        LegendChip(semester = 3)
                        LegendChip(semester = 5)
                    }
                }
            }

            when {
                state.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()

                        }
                    }
                }

                state.jadwal.jadwalView.isNotEmpty() && !state.isLoading -> {
                    items(state.jadwal.jadwalView) { harian ->
                        DaySchedule(harian)
                    }
                }
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
fun DaySchedule(jadwalHarian: DetailJadwalListUI) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(jadwalHarian.nama, fontWeight = FontWeight.Bold)
            Text(
                "${jadwalHarian.item.size} Mata Kuliah",
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (jadwalHarian.item.isEmpty()) {
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
                jadwalHarian.item.forEach {
                    JadwalItem(it)
                }
            }
        }
    }
}

@Composable
fun JadwalItem(jadwal: DetailJadwalItemUI) {
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
                    Text(jadwal.sks.toString(), style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = jadwal.namaJadwal,
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
                    Text(jadwal.namaDosen, style = MaterialTheme.typography.bodySmall)
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
                    Text(jadwal.namaRuangan, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}