package com.polije.sipeperpolije.feature.dashboard.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.core.Semester
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateJadwalDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Semester) -> Unit
) {
    val options =
        mapOf(Semester.GANJIL to "Semester Ganjil", Semester.GENAP to "Semester Genap")

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionSemester: Semester by remember { mutableStateOf(Semester.GANJIL) }
    val semesterTextFieldState = TextFieldState()


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Generate Jadwal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryEditable,
                            true
                        ).fillMaxWidth(),
                        state = semesterTextFieldState,
                        readOnly = true,
                        label = { Text("Semester") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        options.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.value) },
                                onClick = {
                                    selectedOptionSemester = selectionOption.key
                                    semesterTextFieldState.edit {
                                        replace(
                                            0,
                                            length,
                                            selectionOption.value
                                        )
                                    }
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmation(selectedOptionSemester)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}


@Composable
@Preview
fun GenerateJadwalDialogPreview() {
    GenerateJadwalDialog(
        onDismissRequest = {},
        onConfirmation = { _ -> }
    )
}
