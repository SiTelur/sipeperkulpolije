package com.polije.sipeperpolije.feature.master.presentation.jadwal.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun JadwalScreen(jadwalViewModel: JadwalViewModel = koinViewModel()) {

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
                    title = log.semester,
                    semester = log.semester,
                    status = log.isSuccess,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Cari status jadwal...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun FilterChips(
    selectedFilter: GenerationStatus,
    onFilterSelected: (GenerationStatus) -> Unit
) {
    val filters = emptyList<GenerationStatus>() + GenerationStatus.entries.toTypedArray()

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.displayName) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun ListHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Log Generate",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "($count Item)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LogListItem(title: String, semester: String, status: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = semester,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(status = status)
        }
    }
}

@Composable
private fun StatusBadge(status: Boolean) {
    val (icon, color, text) = when (status) {
        true -> Triple(
            Icons.Default.CheckCircle,
            MaterialTheme.colorScheme.tertiary,
            "Sukses"
        )

        false -> Triple(
            Icons.Default.Error,
            MaterialTheme.colorScheme.error,
            "Gagal"
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = text, tint = color, modifier = Modifier.size(14.dp))
        Text(
            text = text.uppercase(),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

