package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component.DosenSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateMataKuliahModal(
    initialMataKuliah: String,
    initialKodeMataKuliah: String,
    initialNamaDosen: String,
    initialSKSTeori: Int,
    initialSKSPraktek: Int,
    initialSemester: Int,
    initialIDDosen: Int?,
    initialIsActive: Boolean,
    modalBottomSheetState: SheetState,
    onDismiss: () -> Unit,
    listDosen: List<DosenUI>,
    onSave: (nama: String, kode: String, sksTeori: Int, sksPraktek: Int, semester: Int, idDosen: Int?, isActive: Boolean) -> Unit
) {
    val namaMataKuliahState = rememberTextFieldState(initialText = initialMataKuliah)
    val kodeMataKuliahState = rememberTextFieldState(initialText = initialKodeMataKuliah)
    val namaDosenState = rememberTextFieldState()
    val sksTeoriState = rememberTextFieldState("$initialSKSTeori")
    val sksPraktekState = rememberTextFieldState("$initialSKSPraktek")
    val semesterState = rememberTextFieldState("Semester ke $initialSemester")
    var expandedSks by remember { mutableStateOf(false) }
    var expandedSemester by remember { mutableStateOf(false) }
    var expandedDosen by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(initialIsActive) }
    val sksOptions = listOf(1, 2, 3, 4, 6)
    val semesterOptions = (1..8)

    var sksTeori = initialSKSTeori
    var sksPraktek = initialSKSPraktek
    var semester = initialSemester
    var idDosen: Int? = initialIDDosen

    val isFormValid by remember {
        derivedStateOf {
            namaMataKuliahState.text.isNotBlank() &&
                    kodeMataKuliahState.text.isNotBlank() &&
                    namaDosenState.text.isNotBlank() &&
                    sksTeoriState.text.toString().toIntOrNull() != null &&
                    sksPraktekState.text.toString().toIntOrNull() != null &&
                    semesterState.text.toString().toIntOrNull() != null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tambah Mata Kuliah",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Text(
                    text = "Lengkapi detail mata kuliah baru di bawah ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    OutlinedTextField(
                        state = namaMataKuliahState,
                        label = { Text("Nama Mata Kuliah") },
                        placeholder = { Text("Contoh: Pemrograman Web") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        state = kodeMataKuliahState,
                        label = { Text("Kode Mata Kuliah") },
                        placeholder = { Text("Contoh: IF-101") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DosenSearchBar(
                        namaDosenState,
                        items = listDosen,
                        label = "Dosen Pengampu",
                        initialText = initialNamaDosen,
                        onItemSelected = {
                            idDosen = it.id
                            namaDosenState.setTextAndSelectAll(it.nama)
                        })

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            state = sksTeoriState,
                            readOnly = true,
                            label = { Text("Jumlah SKS") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            state = sksPraktekState,
                            readOnly = true,
                            label = { Text("Jumlah SKS") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )


                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedSemester,
                        onExpandedChange = { expandedSemester = !expandedSemester },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            state = semesterState,
                            readOnly = true,
                            label = { Text("Semester") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSemester)
                            },
                            modifier = Modifier.menuAnchor(
                                ExposedDropdownMenuAnchorType.PrimaryEditable,
                                enabled = true
                            ).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSemester,
                            onDismissRequest = { expandedSemester = false }
                        ) {
                            semesterOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text("Semester ke $option") },
                                    onClick = {
                                        semesterState.setTextAndSelectAll("Semester ke $option")
                                        semester = option
                                        expandedSemester = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Workshop")
                        Switch(
                            checked = isActive,
                            onCheckedChange = { checked -> isActive = checked })
                    }


                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    enabled = isFormValid,
                    onClick = {
                        onSave(
                            namaMataKuliahState.text.toString(),
                            kodeMataKuliahState.text.toString(),
                            sksTeoriState.text.toString().toInt(),
                            sksPraktekState.text.toString().toInt(),
                            semester, idDosen, isActive
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Simpan Mata Kuliah",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Batal",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}