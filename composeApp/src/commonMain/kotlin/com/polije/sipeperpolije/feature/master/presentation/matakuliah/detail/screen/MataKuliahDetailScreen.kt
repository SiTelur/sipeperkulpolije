package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.component.DeleteConfirmationDialog
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.component.UpdateMataKuliahModal
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahAction
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahEvent
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahViewModel
import com.polije.sipeperpolije.theme.AppTheme
import com.polije.sipeperpolije.utils.ObserveAsEvent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MataKuliahDetailScreen(
    id: Int,
    detailMataKuliahViewModel: DetailMataKuliahViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
    onSuccessAction: () -> Unit,
    onFailedAction: () -> Unit,
    modifier: Modifier = Modifier
) {

    var showEditModal by remember { mutableStateOf(false) }
    var showConfirmationDelete by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val snackbarHost = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    val state by detailMataKuliahViewModel.state.collectAsStateWithLifecycle()



    LaunchedEffect(id) {
        detailMataKuliahViewModel.onAction(DetailMataKuliahAction.OnDetailMataKuliahLoad(id))
    }

    ObserveAsEvent(detailMataKuliahViewModel.events) { event ->
        when (event) {
            is DetailMataKuliahEvent.OnFailureDeleteMataKuliah -> {
                snackbarHost.showSnackbar("Gagal menghapus mata kuliah")
                onFailedAction()
            }

            is DetailMataKuliahEvent.OnFailureUpdateMataKuliah -> {
                snackbarHost.showSnackbar("Gagal mengupdate mata kuliah")
                onFailedAction()
            }

            DetailMataKuliahEvent.OnSuccessDeleteMataKuliah -> {
                snackbarHost.showSnackbar("Berhasil menghapus mata kuliah")
                onSuccessAction()
            }

            DetailMataKuliahEvent.OnSuccessUpdateMataKuliah -> {
                snackbarHost.showSnackbar("Berhasil mengupdate mata kuliah")
                onSuccessAction()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Mata Kuliah",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            showEditModal = true
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Data", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { showConfirmationDelete = true },
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                                alpha = 0.5f
                            ),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hapus Data", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { paddingValues ->

        when {
            state.detail != null && !state.isLoading -> {
                LazyColumn(contentPadding = paddingValues) {
                    item { HeroSection(state.detail!!.nama, semester = state.detail!!.semester) }
                    item { QuickStats(state.detail!!.kode, state.detail!!.jumlahSKS) }
                    item {
                        InfoSection(
                            state.detail!!.namaPenampuPertama,
                            isWorkshop = state.detail!!.isWorkshop
                        )
                    }
                }
            }

            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }


        /**
         *  (){
         *             LazyColumn(contentPadding = paddingValues) {
         *                 item { HeroSection(state.detail, semester = semester) }
         *                 item { QuickStats(kode, sks) }
         *                 item { InfoSection(pengampu, isWorkshop = isWorkshop) }
         *             }
         *         }
         */

        when {
            showEditModal -> {
                state.detail?.let { detail ->
                    UpdateMataKuliahModal(
                        listDosen = state.result,
                        modalBottomSheetState = sheetState,
                        onDismiss = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showEditModal = false
                                }
                            }
                        },
                        initialMataKuliah = detail.nama,
                        initialKodeMataKuliah = detail.kode,
                        initialNamaDosen = detail.namaPenampuPertama,
                        initialJumlahSKS = detail.jumlahSKS,
                        initialSemester = detail.semester,
                        initialIDDosen = detail.idPengampu,
                        initialIsWorkshop = detail.isWorkshop,
                        onSave = { nama, kode, sks, semester, idDosen, isWorkshop ->
                            detailMataKuliahViewModel.onAction(
                                DetailMataKuliahAction.OnDetailMataKuliahUpdate(
                                    id = id,
                                    nama = nama,
                                    kode = kode,
                                    sks = sks,
                                    semester = semester,
                                    idDosen = idDosen,
                                    isWorkshop
                                )
                            )

                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showEditModal = false
                                }
                            }
                        }
                    )
                }
            }

            showConfirmationDelete -> {
                DeleteConfirmationDialog(
                    onDismissRequest = { showConfirmationDelete = false },
                    onConfirm = {
                        showConfirmationDelete = false
                        detailMataKuliahViewModel.onAction(
                            DetailMataKuliahAction.OnDetailMataKuliahDelete(
                                id
                            )
                        )
                    },
                    title = "Hapus", message = "Apakah anda ingin menghapus data mata kuliah ini"
                )
            }

        }
    }
}

@Composable
private fun HeroSection(name: String, semester: Int) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .height(192.dp)
            .fillMaxWidth()
    ) {
        // Placeholder for the background image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Semester $semester",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun QuickStats(kode: String, sks: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Kode",
            value = kode,
            icon = Icons.Default.Fingerprint
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Kredit",
            value = "$sks SKS",
            icon = Icons.Default.School
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoSection(pengampu: String, isWorkshop: Boolean) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(
            icon = Icons.Default.Person,
            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            iconColor = MaterialTheme.colorScheme.primary,
            label = "Dosen Pengampu",
            value = pengampu
        )

        InfoRow(
            icon = Icons.Default.Person,
            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            iconColor = MaterialTheme.colorScheme.primary,
            label = "Workshop",
            value = if (isWorkshop) "Iya" else "Tidak"
        )

    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconColor: Color,
    label: String,
    value: String,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp).weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
    }
}

@Composable
@Preview
fun HeroSectionPreview() {
    AppTheme {
        HeroSection("Logika dan bisnis", 3)
    }
}