package com.polije.sipeperpolije.feature.master.presentation.dosen.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TypeSpecimen
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.feature.master.data.model.TipeDosen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertDosenModal(
    modalBottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSaveAction: (String, String, Boolean, TipeDosen) -> Unit
) {

    val namaDosenTextState = TextFieldState()
    val nidnTextState = TextFieldState()
    val (tipeDosen, setTipeDosen) = remember { mutableStateOf(TipeDosen.TETAP) }
    var expandedTipeDosen by remember { mutableStateOf(false) }
    val tipeDosenState = TextFieldState(tipeDosen.name.replace("_", " "))
    val (isActive, setIsActive) = remember { mutableStateOf(true) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tambah Data Dosen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onDismissRequest() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    state = namaDosenTextState,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nama Dosen") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Nama Dosen")
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    state = nidnTextState,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("NIDN") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = "NIDN")
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedTipeDosen,
                    onExpandedChange = { expandedTipeDosen = !expandedTipeDosen }
                ) {
                    OutlinedTextField(
                        state = tipeDosenState,
                        readOnly = true,
                        label = { Text("Tipe Dosen") },
                        leadingIcon = {
                            Icon(Icons.Default.TypeSpecimen, contentDescription = "Tipe Dosen")
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipeDosen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                            true
                        ).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipeDosen,
                        onDismissRequest = { expandedTipeDosen = false }) {
                        TipeDosen.entries.forEach { tipeDosen ->
                            DropdownMenuItem({ Text(tipeDosen.name.replace("_", " ")) }, onClick = {
                                setTipeDosen(tipeDosen)
                                expandedTipeDosen = false

                            })
                        }

                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Aktif")
                    Switch(isActive, onCheckedChange = { setIsActive(it) })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal", fontWeight = FontWeight.Bold)
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = {
                            onSaveAction(
                                namaDosenTextState.text.toString(),
                                nidnTextState.text.toString(),
                                isActive,
                                tipeDosen
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tambah Data", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}