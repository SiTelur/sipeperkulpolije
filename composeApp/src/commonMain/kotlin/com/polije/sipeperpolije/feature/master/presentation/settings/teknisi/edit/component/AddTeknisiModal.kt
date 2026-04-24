package com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.feature.master.presentation.settings.teknisi.edit.viewmodel.TeknisiUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeknisiModal(
    modifier: Modifier = Modifier,
    modalBottomSheetState: SheetState,
    onSave: (teknisi: TeknisiUI) -> Unit,
    onDismissRequest: () -> Unit
) {
    val namaTeknisi = rememberTextFieldState()
    var isActive by remember { mutableStateOf(true) }

    val isFormValid by remember {
        derivedStateOf {
            namaTeknisi.text.isNotBlank()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 24.dp, start = 16.dp, end = 16.dp).verticalScroll(
                rememberScrollState()
            )
        ) {
            Text(
                "Tambah Teknisi",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Isi data teknisi baru",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                namaTeknisi,
                label = { Text("Nama Teknisi") },
                placeholder = { Text("Masukkan nama teknisi") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status Aktif")
                Switch(checked = isActive, onCheckedChange = { isActive = it })
            }

            Button(
                enabled = isFormValid,
                onClick = {
                    onSave(
                        TeknisiUI(
                            id = 0,
                            nama = namaTeknisi.text.toString(),
                            isActive = isActive
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Simpan Teknisi",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Batal",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}