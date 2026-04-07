package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.RuanganUI
import com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.viewmodel.TipeRuangan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuanganModal(
    modifier: Modifier = Modifier,
    modalBottomSheetState: SheetState,
    onSave: (hari: RuanganUI) -> Unit,
    onDismissRequest: () -> Unit
) {

    val namaRuangan = rememberTextFieldState()
    var (tipeRuangan, setTipeRuangan) = remember { mutableStateOf(TipeRuangan.TEORI) }
    val tipeRuanganText = rememberTextFieldState(tipeRuangan.label)

    val isFormValid by remember {
        derivedStateOf {
            namaRuangan.text.isNotBlank() && tipeRuanganText.text.isNotBlank()
        }
    }


    var isExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 12.dp, start = 8.dp, end = 8.dp).verticalScroll(
                rememberScrollState()
            )
        ) {
            Text(
                "Tambah Ruangan",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Isi data ruangan dengan tipe ruangannya",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                namaRuangan,
                label = { Text("Nama Ruangan") },
                placeholder = { Text("Lab RSI") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(isExpanded, { isExpanded = !isExpanded }) {
                OutlinedTextField(
                    tipeRuanganText,
                    label = {
                        Text(
                            "Jenis Ruangan",
                        )
                    },
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) },
                    modifier = Modifier.menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                        true
                    ).fillMaxWidth()
                )
                DropdownMenu(isExpanded, onDismissRequest = {
                    isExpanded = false
                }) {
                    TipeRuangan.entries.forEach {
                        DropdownMenuItem(text = { Text(it.label) }, onClick = {
                            tipeRuanganText.edit {
                                replace(
                                    0,
                                    tipeRuanganText.text.length,
                                    it.label
                                )
                            }
                            isExpanded = false
                            setTipeRuangan(it)
                        })
                    }
                }
            }

            Button(
                enabled = isFormValid,
                onClick = {
                    onSave(
                        RuanganUI(
                            0,
                            namaRuangan.text.toString(),
                            tipeRuangan
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Simpan Ruangan",
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
