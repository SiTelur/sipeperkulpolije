package com.polije.sipeperpolije.feature.master.presentation.matakuliah.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.presentation.component.MataKuliahListItem

data class MataKuliah(
    val code: String,
    val name: String,
    val sks: Int,
    val lecturer: String,
)

val sampleMataKuliahList = listOf(
    MataKuliah("IF2024", "Pemrograman Web", 3, "Dr. Budi Santoso, M.Kom"),
    MataKuliah("IF3050", "Algoritma & Struktur Data", 4, "Prof. Siti Aminah, Ph.D"),
    MataKuliah("IF1010", "Dasar Sistem Komputer", 3, "Bambang S.T., M.T."),
    MataKuliah("IF2200", "Matematika Diskrit", 3, "Dr. Eka Putra"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MataKuliahScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = sampleMataKuliahList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.code.contains(
            searchQuery,
            ignoreCase = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Mata Kuliah", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Handle add action */ }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah Mata Kuliah",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredList) { mataKuliah ->
                MataKuliahListItem(mataKuliah = mataKuliah)
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        placeholder = { Text("Cari kode atau nama mata kuliah...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        )
    )
}



