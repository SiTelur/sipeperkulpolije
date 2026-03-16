package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.InsertMataKuliahModal
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.MataKuliahListItem
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.MataKuliahSearchBar
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.MataKuliahStatus
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.MataKuliahStatusChip
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahAction
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahEvent
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.ListMataKuliahViewModel
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MataKuliahScreen(
    listMataKuliahViewModel: ListMataKuliahViewModel = koinViewModel(),
    resultFromDetail: Boolean?,
    onItemClick: (MataKuliahUI) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showInsertModal by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackBarHost = LocalSnackbarHostState.current

    val state by listMataKuliahViewModel.state.collectAsStateWithLifecycle()

    val (mataKuliahStatus, setMataKuliahStatus) = remember { mutableStateOf(MataKuliahStatus.SEMUA) }
    val (searchMataKuliahStatus, setSearchMataKuliahStatus) = remember { mutableStateOf("") }

    ObserveAsEvent(listMataKuliahViewModel.events) { event ->
        when (event) {
            is ListMataKuliahEvent.OnSaveSuccess -> {
                val snackBar = snackBarHost.showSnackbar(
                    "Mata kuliah berhasil ditambahkan",
                    actionLabel = "Lihat Data",
                    duration = SnackbarDuration.Long
                )
                when (snackBar) {
                    SnackbarResult.ActionPerformed -> {
                        onItemClick(event.data)
                    }

                    SnackbarResult.Dismissed -> {

                    }
                }
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
        resultFromDetail?.let {
            if (it) {
                listMataKuliahViewModel.loadItems()
            }
        }
    }

    val lazyListState = rememberLazyListState()

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

            item {
                Column {
                    MataKuliahSearchBar(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        searchQuery,
                        {
                            searchQuery = it
                            listMataKuliahViewModel.onAction(ListMataKuliahAction.OnSearcDosen(it))
                        }
                    )
                    MataKuliahStatusChip(
                        selectedFilter = mataKuliahStatus,
                        {
                            setMataKuliahStatus(it)
                            listMataKuliahViewModel.onAction(
                                ListMataKuliahAction.OnChangeStatusChip(
                                    it.status
                                )
                            )
                        })
                }
            }
            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(state.filteredMatakuliahs, key = { it.id }) { item ->
                MataKuliahListItem(mataKuliah = item, onItemClick = { onItemClick(item) })
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
                    onSave = { nama, kode, sksTeori, sksPraktek, semester, dosenID, isWorkshop, namaDosen ->
                        listMataKuliahViewModel.onAction(
                            ListMataKuliahAction.OnSaveMataKuliah(
                                nama,
                                kode,
                                sksTeori, sksPraktek,
                                semester, dosenID, namaDosen = namaDosen, isWorkshop
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
                    listDosen = state.result
                )
            }
        }
    }
}



