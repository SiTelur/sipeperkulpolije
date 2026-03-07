package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component.AddRuanganModal
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component.ClassItem
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component.EditRuanganModal
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component.RuanganAlertDialog
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.EditRuanganViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.RuanganAction
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.RuanganEvent
import com.polije.sipeperpolije.theme.AppTheme
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRuanganScreen(
    editRuanganViewModel: EditRuanganViewModel = koinViewModel(),
    onBackButtonPressed: () -> Unit
) {
    val state by editRuanganViewModel.ruangan.collectAsStateWithLifecycle()
    val snackbarHost = LocalSnackbarHostState.current
    val editSheetState = rememberModalBottomSheetState()
    val addSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val (selectedId, setSelectedId) = remember { mutableStateOf<Int?>(null) }

    ObserveAsEvent(editRuanganViewModel.events) { event ->
        when (event) {
            is RuanganEvent.OnUpdateSuccess -> {
                editSheetState.hide()
            }

            is RuanganEvent.OnFailure -> {
                snackbarHost.showSnackbar(event.message)
            }

            RuanganEvent.OnDeleteSuccess -> {
                snackbarHost.showSnackbar("Berhasil menghapus ruangan")
            }

            RuanganEvent.OnInsertSuccess -> {
                snackbarHost.showSnackbar("Berhasil menambahkan ruangan")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Availability", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackButtonPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showBottomSheet = true
                    }) {
                        Icon(Icons.Default.Add, "Add Ruangan")
                    }
                }
            )
        }, snackbarHost = {
            SnackbarHost(snackbarHost)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Set your weekly schedule",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Configure your active working days and hours for the upcoming week.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(state.listRuangan, key = { it.id }) {
                ClassItem(it.id, it.nama, { id ->
                    setSelectedId(id)
                }) {
                    editRuanganViewModel.onAction(RuanganAction.OnRuanganSelected(it))
                    scope.launch {
                        editSheetState.show()
                    }
                }
            }
            item {
                Spacer(Modifier.height(80.dp)) // Spacer for bottom bar
            }
        }
    }

    when {
        showBottomSheet -> {
            AddRuanganModal(
                modalBottomSheetState = addSheetState,
                onSave = {
                    editRuanganViewModel.onAction(RuanganAction.OnInsertRuangan(it))
                    scope.launch {
                        addSheetState.hide()
                    }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                onDismissRequest = {
                    scope.launch {
                        addSheetState.hide()
                    }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                }
            )
        }
    }

    selectedId?.let { id ->
        RuanganAlertDialog(onDismissRequest = {
            setSelectedId(null)
        }, onConfirmation = {
            editRuanganViewModel.onAction(RuanganAction.OnDeleteRuangan(id))
            setSelectedId(null)
        }, "Hapus Ruangan", "Apakah anda yakin ingin menghapus ruangan ini?")
    }

    state.selectedRuangan?.let {
        EditRuanganModal(
            modalBottomSheetState = editSheetState,
            id = it.id,
            initialNamaRuangan = it.nama,
            initialTipeRuangan = it.tipeRuangan, onDismissRequest = {
                scope.launch {
                    editSheetState.hide()
                }.invokeOnCompletion {
                    editRuanganViewModel.onAction(RuanganAction.OnDismissRuangan)
                }
            }, onSave = { ruangan ->
                editRuanganViewModel.onAction(RuanganAction.OnUpdateRuangan(ruangan))
            })
    }
}


@Preview
@Composable
fun ClassItemPreview() {
    AppTheme {
        ClassItem(1, "Aula", {}) {}
    }
}

@Preview
@Composable
fun RuanganAlertDialogPreview() {
    AppTheme {
        RuanganAlertDialog(onDismissRequest = {

        }, onConfirmation = {}, "Hapus Ruangan", "Apakah anda yakin ingin menghapus ruangan ini?")
    }
}


