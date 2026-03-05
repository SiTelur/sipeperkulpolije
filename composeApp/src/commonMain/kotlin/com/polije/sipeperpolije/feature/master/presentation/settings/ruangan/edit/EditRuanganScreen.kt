package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.EditRuanganViewModel
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.RuanganAction
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.RuanganEvent
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
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ObserveAsEvent(editRuanganViewModel.events) { event ->
        when (event) {
            is RuanganEvent.OnUpdateSuccess -> {
                sheetState.hide()
            }

            is RuanganEvent.OnUpdateFailure -> {
                sheetState.hide()
                snackbarHost.showSnackbar(event.message)
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
            items(state.listRuangan) {
                ClassItem(it.nama) {
                    editRuanganViewModel.onAction(RuanganAction.OnRuanganSelected(it))
                    scope.launch {
                        sheetState.show()
                    }
                }
            }
            item {
                Spacer(Modifier.height(80.dp)) // Spacer for bottom bar
            }
        }
    }

    state.selectedRuangan?.let {

    }
}

@Composable
private fun ClassItem(nama: String, onDayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onDayClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    nama,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


