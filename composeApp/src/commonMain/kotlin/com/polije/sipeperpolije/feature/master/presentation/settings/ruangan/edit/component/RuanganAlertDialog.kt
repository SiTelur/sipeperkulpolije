package com.polije.sipeperpolije.feature.master.presentation.settings.ruangan.edit.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun RuanganAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: String,
    message: String
) {
    AlertDialog(
        onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirmation) { Text("Konfirmasi") } },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Batal") } })
}