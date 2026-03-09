package com.polije.sipeperpolije.feature.master.presentation.jadwal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
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
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.component.JadwalItem
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalAction
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalEvent
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalListUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.viewmodel.DetailJadwalViewModel
import com.polije.sipeperpolije.utils.ObserveAsEvent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailJadwalScreen(
    id: Int,
    detailJadwalViewModel: DetailJadwalViewModel = koinViewModel(),
    onBackPressed: () -> Unit
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

            is DetailJadwalEvent.OnFailureDownloadJadwal -> {
                snackbarHost.showSnackbar("Berhasil mengunduh jadwal")
            }

            DetailJadwalEvent.OnSuccessDownloadJadwal -> {
                snackbarHost.showSnackbar("Gagal mengunduh jadwal")

            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Jadwal Akademik", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        detailJadwalViewModel.onAction(
                            DetailJadwalAction.OnDownloadJadwal(
                                id
                            )
                        )
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Filter")
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
            stickyHeader {
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
