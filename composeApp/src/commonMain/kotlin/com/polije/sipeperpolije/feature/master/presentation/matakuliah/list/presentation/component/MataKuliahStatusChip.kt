package com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class MataKuliahStatus(val displayName: String, val status: Boolean?) {
    SEMUA("Semua", null),
    AKTIF("Aktif", true),
    NONAKTIF("Nonaktif", false),
}

@Composable
fun MataKuliahStatusChip(
    selectedFilter: MataKuliahStatus,
    onFilterSelected: (MataKuliahStatus) -> Unit
) {
    val filters = emptyList<MataKuliahStatus>() + MataKuliahStatus.entries.toTypedArray()

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.displayName) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}