package com.polije.sipeperpolije.feature.master.presentation.jadwal.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.component.FilterChips
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.component.ListHeader
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.component.LogListItem
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.component.SearchBar
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.JadwalViewModel
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.viewmodel.ListJadwalAction
import org.koin.compose.viewmodel.koinViewModel

enum class GenerationStatus(val displayName: String, val status: Boolean?) {
    SEMUA("Semua", null),
    SUKSES("Sukses", true),
    GAGAL("Gagal", false),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalScreen(
    jadwalViewModel: JadwalViewModel = koinViewModel(),
    onItemClick: (id: Int) -> Unit
) {

    val state by jadwalViewModel.state.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(GenerationStatus.SEMUA) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Generate Jadwal", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Refresh logs */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            item {
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            }


            item {
                FilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelected = {
                        selectedFilter = it
                        jadwalViewModel.onAction(ListJadwalAction.ChangeGenerationStatus(it.status))
                    }
                )
            }
            item {
                ListHeader(count = state.filteredJadwal.size)
            }
            items(state.filteredJadwal) { log ->
                LogListItem(
                    title = log.title,
                    semester = log.semester,
                    status = log.isSuccess,
                    onClick = { onItemClick(log.id) }
                )
            }
        }
    }
}









