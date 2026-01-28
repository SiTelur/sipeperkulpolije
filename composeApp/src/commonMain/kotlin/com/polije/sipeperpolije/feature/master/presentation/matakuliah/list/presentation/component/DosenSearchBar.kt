package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenSearchBar(
    namaDosenState: TextFieldState,
    items: List<DosenUI>,
    label: String,
    modifier: Modifier = Modifier,
    onItemSelected: (DosenUI) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredItems = remember(namaDosenState.text.toString(), items) {
        if (namaDosenState.text.toString().isBlank()) items
        else items.filter {
            it.nama.contains(namaDosenState.text.toString(), ignoreCase = true) or it.nidn.contains(
                namaDosenState.text.toString(),
                ignoreCase = true
            )
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = namaDosenState.text.toString(),
            onValueChange = {
                namaDosenState.edit { replace(0, length, it) }
            },
            label = { Text("Nama Dosen") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor(
                ExposedDropdownMenuAnchorType.PrimaryEditable,
                enabled = true
            ).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.nama) },
                    onClick = {
                        namaDosenState.setTextAndSelectAll(item.nama)
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }


    }

}