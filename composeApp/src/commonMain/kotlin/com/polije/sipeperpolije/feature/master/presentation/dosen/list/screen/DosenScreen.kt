package com.polije.sipeperpolije.feature.master.presentation.dosen.list.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.component.DosenListItem
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.component.InsertDosenModal
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.ListDosenAction
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.ListDosenEvent
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.ListDosenViewModel
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenScreen(
    dosenListViewModel: ListDosenViewModel = koinViewModel(),
    resultFromDetail: Boolean?,
    onListItemClick: (DosenUI) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val snackBarState = LocalSnackbarHostState.current
    val state by dosenListViewModel.state.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showInsertDosenModal by remember { mutableStateOf(false) }

    ObserveAsEvent(dosenListViewModel.events) {
        when (it) {
            is ListDosenEvent.OnLoadError -> {
                snackBarState.showSnackbar(it.toString())
            }

            is ListDosenEvent.OnSaveError -> {}
            ListDosenEvent.OnSaveSuccess -> {
                dosenListViewModel.resetItems()
            }
        }
    }

    LaunchedEffect(resultFromDetail) {
        if (resultFromDetail == true) {
            dosenListViewModel.resetItems()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            TopAppBar(
                title = { Text("Daftar Dosen", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { showInsertDosenModal = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Dosen")
                    }
                }
            )
        },
    ) { paddingValues ->
        val lazyListState = rememberLazyListState()

        LaunchedEffect(state.dosens) {
            snapshotFlow {
                lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            }.distinctUntilChanged().collect {
                if (it == state.dosens.lastIndex) {
                    dosenListViewModel.loadNextItems()
                }
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.dosens, key = { it.id }) {
                DosenListItem(it.initial, name = it.nama, nidn = it.nidn) {
                    onListItemClick(it)
                }
            }

            if (state.isLoadingMore) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        when {
            showInsertDosenModal -> {
                InsertDosenModal(
                    modalBottomSheetState = modalBottomSheetState,
                    onDismissRequest = {
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showInsertDosenModal = false
                            }
                        }
                    }, onSaveAction = { nama, nidn ->
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showInsertDosenModal = false
                            }
                        }

                        dosenListViewModel.onAction(ListDosenAction.InsertDosen(nama, nidn))
                    })

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
