package com.polije.sipeperpolije.feature.master.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.viewmodel.dosen.ListDosenEvent
import com.polije.sipeperpolije.feature.master.presentation.viewmodel.dosen.ListDosenViewModel
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

data class Dosen(val name: String, val nidn: String, val faculty: String, val initials: String)

val sampleDosenList = listOf(
    Dosen("Dr. Budi Santoso", "12345678", "Fakultas Ilmu Komputer", "BS"),
    Dosen("Prof. Siti Aminah", "87654321", "Fakultas Ekonomi", "SA"),
    Dosen("Andi Pratama, M.Kom", "11223344", "Fakultas Teknik", "AP"),
    Dosen("Joko Susilo, Ph.D", "99887766", "Fakultas Hukum", "JS"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenScreen(dosenListViewModel: ListDosenViewModel = koinViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val snackBarState = LocalSnackbarHostState.current
    val state by dosenListViewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvent(dosenListViewModel.events) {
        when (it) {
            is ListDosenEvent.OnLoadError -> {
                snackBarState.showSnackbar(it.toString())
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            TopAppBar(
                title = { Text("Daftar Dosen", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Handle FAB click */ }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Dosen")
            }
        }
    ) { paddingValues ->
        val lazyListState = rememberLazyListState()

        LaunchedEffect(state.dosens) {
            snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.distinctUntilChanged()
                .collect { lastVisibleIndex ->
                    if (lastVisibleIndex == state.dosens.lastIndex) {
                        dosenListViewModel.loadNextItems()
                    }
                }

        }
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            items(state.dosens, key = { it.id }) {
                DosenListItem(it.initial, name = it.nama)
            }

            if (state.isLoadingMore) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
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
            .padding(16.dp),
        placeholder = { Text("Cari nama atau NIDN...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun ListHeader(count: Int) {
    Text(
        text = "Jumlah Dosen ($count)",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun DosenListItem(initials: String, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle item click */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
//            Text(
//                text = "NIDN: ${dosen.nidn}",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//            Text(
//                text = dosen.faculty,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
//            )
        }

        IconButton(onClick = { /* TODO: Handle more options */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
        }
    }
}