package com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.component

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHariModal(
    modifier: Modifier = Modifier,
    modalBottomSheetState: SheetState,
    initialNamaHari: String,
    initialJamMulai: Int,
    initialJamSelesai: Int,
    initialJamMulaiIstirahat: Int?,
    initialJamSelesaiIstirahat: Int?,
    onDismissRequest: () -> Unit
) {

    val namaHari = rememberTextFieldState(initialNamaHari)
    val jamMulai = rememberTextFieldState(initialJamMulai.toString())
    val jamSelesai = rememberTextFieldState(initialJamSelesai.toString())
    val jamMulaiIstirahat = rememberTextFieldState(initialJamMulaiIstirahat.toString())
    val jamSelesaiIstirahat = rememberTextFieldState(initialJamSelesaiIstirahat.toString())

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
                "Ubah Jadwal Hari",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Isi dengan memasukkan data dalam bentuk jam",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                namaHari,
                readOnly = true,
                label = { Text("Nama Hari") },
                placeholder = { Text("Senin") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    jamMulai,
                    label = {
                        Text(
                            "Jam Mulai Pelajaran",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    placeholder = { Text("Contoh: 7") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    jamSelesai,
                    label = {
                        Text(
                            "Jam Selesai Pelajaran",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    placeholder = { Text("Contoh: 17") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    jamMulaiIstirahat,
                    label = {
                        Text(
                            "Jam Mulai Istirahat",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    placeholder = { Text("Contoh: 7") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    jamSelesaiIstirahat,
                    label = {
                        Text(
                            "Jam Selesai Pelajaran",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    placeholder = { Text("Contoh: 17") },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                enabled = true,
                onClick = {

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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EditHariModalPreview() {
    AppTheme {
        EditHariModal(
            modalBottomSheetState = rememberModalBottomSheetState(),
            initialNamaHari = "Senin",
            initialJamMulai = 7,
            initialJamSelesai = 17,
            initialJamMulaiIstirahat = 12,
            initialJamSelesaiIstirahat = 13,
            onDismissRequest = {},

        )
    }
}