package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component.RuanganAlertDialog
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.component.AddTeknisiModal
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.component.EditTeknisiModal
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.component.TeknisiItem
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel.EditTeknisiViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel.TeknisiAction
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel.TeknisiEvent
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeknisiScreen(
    viewModel: EditTeknisiViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    val addSheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()

    var showAddSheet by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Int?>(null) }

    ObserveAsEvent(viewModel.events) { event ->
        when (event) {
            is TeknisiEvent.OnFailure -> {
                snackbarHost.showSnackbar(event.message)
            }

            TeknisiEvent.OnDeleteSuccess -> {
                snackbarHost.showSnackbar("Berhasil menghapus teknisi")
            }

            TeknisiEvent.OnInsertSuccess -> {
                snackbarHost.showSnackbar("Berhasil menambahkan teknisi")
            }

            TeknisiEvent.OnUpdateSuccess -> {
                snackbarHost.showSnackbar("Berhasil mengubah teknisi")
                scope.launch { editSheetState.hide() }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teknisi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked,
                        content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") })
                },
                actions = {
                    IconButton(onClick = { showAddSheet = true }) {
                        Icon(Icons.Default.Add, "Tambah Teknisi")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Daftar Teknisi",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Manajemen teknisi yang tersedia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(state.listTeknisi, key = { it.id }) { teknisi ->
                        TeknisiItem(
                            id = teknisi.id,
                            nama = teknisi.nama,
                            isActive = teknisi.isActive,
                            onDelete = { deleteId = it },
                            onEdit = {
                                viewModel.onAction(TeknisiAction.OnTeknisiSelected(teknisi))
                                scope.launch { editSheetState.show() }
                            }
                        )
                    }
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddTeknisiModal(
            modalBottomSheetState = addSheetState,
            onSave = {
                viewModel.onAction(TeknisiAction.OnInsertTeknisi(it))
                scope.launch { addSheetState.hide() }.invokeOnCompletion { showAddSheet = false }
            },
            onDismissRequest = {
                scope.launch { addSheetState.hide() }.invokeOnCompletion { showAddSheet = false }
            }
        )
    }

    state.selectedTeknisi?.let { teknisi ->
        EditTeknisiModal(
            modalBottomSheetState = editSheetState,
            id = teknisi.id,
            initialNamaTeknisi = teknisi.nama,
            initialIsActive = teknisi.isActive,
            onSave = {
                viewModel.onAction(TeknisiAction.OnUpdateTeknisi(it))
                scope.launch { editSheetState.hide() }.invokeOnCompletion {
                    viewModel.onAction(TeknisiAction.OnDismissTeknisi)
                }
            },
            onDismissRequest = {
                scope.launch { editSheetState.hide() }.invokeOnCompletion {
                    viewModel.onAction(TeknisiAction.OnDismissTeknisi)
                }
            }
        )
    }

    deleteId?.let { id ->
        RuanganAlertDialog(
            onDismissRequest = { deleteId = null },
            onConfirmation = {
                viewModel.onAction(TeknisiAction.OnDeleteTeknisi(id))
                deleteId = null
            },
            title = "Hapus Teknisi",
            message = "Apakah anda yakin ingin menghapus teknisi ini?"
        )
    }
}