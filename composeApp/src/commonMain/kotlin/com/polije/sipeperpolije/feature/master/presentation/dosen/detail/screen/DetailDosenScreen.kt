package com.polije.sipeperpolije.feature.master.presentation.dosen.detail.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.component.DeleteConfirmationDialog
import com.polije.sipeperpolije.feature.master.presentation.dosen.detail.component.UpdateDosenModal
import com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel.DetailDosenAction
import com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel.DetailDosenEvent
import com.polije.sipeperpolije.feature.master.presentation.dosen.detail.viewmodel.DetailDosenViewModel
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailDosenScreen(
    dosenUI: DosenUI,
    detailDosenViewModel: DetailDosenViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onSuccessAction: () -> Unit,
    onFailureAction: () -> Unit
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val state by detailDosenViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(dosenUI) {
        detailDosenViewModel.onAction(DetailDosenAction.OnInitial(dosenUI.id))
    }

    ObserveAsEvent(detailDosenViewModel.events) { event ->
        when (event) {
            is DetailDosenEvent.OnDosenSuccessAction -> {
                snackbarHostState.showSnackbar("Berhasil mengubah atau menghapus data dosen")
                onSuccessAction()
            }

            is DetailDosenEvent.OnDosenFailedAction -> {
                snackbarHostState.showSnackbar("Gagal mengubah atau menghapus data dosen")
                onFailureAction()
            }

            is DetailDosenEvent.OnMataKuliahFetchFailed -> {
                val result = snackbarHostState.showSnackbar(
                    "Gagal mengambil data mata kuliah untuk dosen ${dosenUI.nama}",
                    actionLabel = "Muat Ulang"
                )

                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        detailDosenViewModel.onAction(DetailDosenAction.OnInitial(dosenUI.id))
                    }

                    SnackbarResult.Dismissed -> {

                    }
                }
            }

        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Dosen", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row {
                    Button(
                        onClick = { showBottomSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Ubah Data Dosen", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showConfirmationDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Hapus Data Dosen", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { ProfileHeader(dosenUI.nama) }
            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                )
            }
            item { AcademicInfoSection(dosenUI.nidn) }
            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                )
            }
            item { MataKuliahSection(state.listMataKuliah) }
        }

        when {
            showBottomSheet -> {
                UpdateDosenModal(
                    namaDosenTextState = TextFieldState(initialText = dosenUI.nama),
                    nidnTextState = TextFieldState(initialText = dosenUI.nidn),
                    modalBottomSheetState, onDismissRequest = {
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }, onSaveAction = { newNama, newNIDN ->
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }

                        detailDosenViewModel.onAction(
                            DetailDosenAction.OnDosenUpdate(
                                DosenUI(
                                    dosenUI.id,
                                    newNama,
                                    newNIDN
                                )
                            )
                        )
                    })
            }

            showConfirmationDialog -> {
                DeleteConfirmationDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    onConfirm = {
                        showConfirmationDialog = false
                        detailDosenViewModel.onAction(
                            DetailDosenAction.OnDosenDelete(
                                dosenUI.id
                            )

                        )
                    },
                    title = "Hapus", message = "Apakah anda ingin menghapus data dosen ini"
                )
            }
        }

    }
}

@Composable
private fun ProfileHeader(nama: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = nama,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
//            Text(
//                text = "Dosen Tetap • Aktif",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
        }
    }
}

@Composable
private fun AcademicInfoSection(nidn: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.School,
                contentDescription = "Academic Info",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Informasi Akademik",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        InfoRow("NIDN", nidn)
//        InfoRow("Program Studi", "Teknik Informatika")
//        InfoRow("Jabatan Fungsional", "Lektor Kepala")
//        InfoRow("Pendidikan Terakhir", "S3 Ilmu Komputer")
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
}

@Composable
private fun MataKuliahSection(list: List<MataKuliahUI>) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.ContactPhone,
                contentDescription = "Contact Info",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Mata Kuliah",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(0.dp))
        list.forEach {
            MataKuliahDosenItem(it.kode, it.nama)
        }
    }
}

@Composable
fun MataKuliahDosenItem(
    kode: String,
    nama: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    kode,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    nama,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun MataKuliahDosenItemPreview() {
    MataKuliahDosenItem(kode = "Mata Kuliah", nama = "Algoritma dan Pemrograman")
}

