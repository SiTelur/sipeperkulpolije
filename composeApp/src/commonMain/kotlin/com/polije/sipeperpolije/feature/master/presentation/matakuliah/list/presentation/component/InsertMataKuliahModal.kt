package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component

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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertMataKuliahModal(
    modalBottomSheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (nama: String, kode: String, sks: Int, semester: Int) -> Unit
) {
    val namaMataKuliahState = TextFieldState()
    val kodeMataKuliahState = TextFieldState()
    val sksState = TextFieldState()
    var semesterState = TextFieldState()
    var expandedSks by remember { mutableStateOf(false) }
    var expandedSemester by remember { mutableStateOf(false) }
    val sksOptions = listOf(1, 2, 3, 4, 6)
    val semesterOptions = (1..8)

    var namaMataKuliah = ""
    var kodeMataKuliah = ""
    var sks = 0
    var semester = 0

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
                Text(
                    text = "Tambah Mata Kuliah",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expandedSks,
                            onExpandedChange = { expandedSks = !expandedSks },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                state = sksState,
                                readOnly = true,
                                label = { Text("Jumlah SKS") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSks)
                                },
                                modifier = Modifier.menuAnchor(
                                    ExposedDropdownMenuAnchorType.PrimaryEditable,
                                    enabled = true
                                ).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedSks,
                                onDismissRequest = { expandedSks = false }
                            ) {
                                sksOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("$option SKS") },
                                        onClick = {
                                            sksState.setTextAndSelectAll("$option SKS")
                                            sks = option
                                            expandedSks = false
                                        }
                                    )
                                }
                            }
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
                                        text = { Text("Semester $option") },
                                        onClick = {
                                            semesterState.setTextAndSelectAll("$option")
                                            semester = option
                                            expandedSemester = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSave(
                            namaMataKuliahState.text.toString(),
                            kodeMataKuliahState.text.toString(),
                            sks,
                            semester
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
