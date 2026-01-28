package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.InsertMataKuliahModal
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.MataKuliahListItem
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahAction
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahEvent
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahViewModel
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

data class MataKuliah(
    val code: String,
    val name: String,
    val sks: Int,
    val lecturer: String,
)

val sampleMataKuliahList = listOf(
    MataKuliah("IF2024", "Pemrograman Web", 3, "Dr. Budi Santoso, M.Kom"),
    MataKuliah("IF3050", "Algoritma & Struktur Data", 4, "Prof. Siti Aminah, Ph.D"),
    MataKuliah("IF1010", "Dasar Sistem Komputer", 3, "Bambang S.T., M.T."),
    MataKuliah("IF2200", "Matematika Diskrit", 3, "Dr. Eka Putra"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MataKuliahScreen(
    listMataKuliahViewModel: ListMataKuliahViewModel = koinViewModel(),
    resultFromDetail: Boolean? = null,
    onItemClick: (MataKuliahUI) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showInsertModal by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackBarHost = LocalSnackbarHostState.current

    val state by listMataKuliahViewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvent(listMataKuliahViewModel.events) { event ->
        when (event) {
            is ListMataKuliahEvent.OnSaveSuccess -> {
                snackBarHost.showSnackbar("Mata kuliah berhasil ditambahkan")
                onItemClick(event.data)
            }

            is ListMataKuliahEvent.OnLoadError -> {
                snackBarHost.showSnackbar(event.message)
            }

            is ListMataKuliahEvent.OnSaveFailure -> {
                snackBarHost.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(resultFromDetail) {
        if (resultFromDetail == true) {
            listMataKuliahViewModel.resetItems()
        }
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.mataKuliahs) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect {
            if (it == state.mataKuliahs.lastIndex) {
                listMataKuliahViewModel.loadNextItems()
            }
        }
    }




    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Daftar Mata Kuliah", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showInsertModal = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah Mata Kuliah",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.mataKuliahs, key = { it.id }) { item ->
                MataKuliahListItem(mataKuliah = item, onItemClick = { onItemClick(item) })
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
            showInsertModal -> {
                InsertMataKuliahModal(
                    modalBottomSheetState = modalBottomSheetState,
                    onDismiss = {
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showInsertModal = false
                            }
                        }
                    },
                    onSave = { nama, kode, sks, semester, dosenID ->
                        listMataKuliahViewModel.onAction(
                            ListMataKuliahAction.OnSaveMataKuliah(
                                nama,
                                kode,
                                sks,
                                semester,
                                dosenID
                            )
                        )

                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showInsertModal = false
                            }
                        }
                    },
                    onDosenSearch = { value ->
                        listMataKuliahViewModel.onAction(
                            ListMataKuliahAction.OnSearcDosen(
                                value
                            )
                        )
                    },
                    listDosen = state.result
                )
            }
        }
    }
}



