package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI
import com.polije.sipeperpolije.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun MataKuliahListItem(

    mataKuliah: MataKuliahUI,
    onItemClick: (MataKuliahUI) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .clickable { onItemClick(mataKuliah) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CourseIcon(code = mataKuliah.kode)

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = mataKuliah.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    SksChip(sks = mataKuliah.jumlahSKS)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mataKuliah.namaPenampuPertama,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opsi Lainnya",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
@Preview
fun MataKuliahListItemPreview() {
    AppTheme {
        MataKuliahListItem(
            mataKuliah = MataKuliahUI(
                kode = "TIF120706",
                id = 1,
                jumlahSKS = 3,
                namaPenampuPertama = "Dimas",
                nama = "Pemrograman Mobile",
                semester = 2,
                idPengampu = 1
            ),

            onItemClick = {},
        )
    }
}
