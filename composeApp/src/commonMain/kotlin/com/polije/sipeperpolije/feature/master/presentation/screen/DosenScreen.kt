package com.polije.sipeperpolije.feature.master.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.polije.sipeperpolije.feature.master.presentation.viewmodel.ListDosenViewModel
import org.koin.compose.viewmodel.koinViewModel

data class Dosen(val name: String, val nidn: String, val faculty: String, val initials: String)

val sampleDosenList = listOf(
    Dosen("Dr. Budi Santoso", "12345678", "Fakultas Ilmu Komputer", "BS"),
    Dosen("Prof. Siti Aminah", "87654321", "Fakultas Ekonomi", "SA"),
    Dosen("Andi Pratama, M.Kom", "11223344", "Fakultas Teknik", "AP"),
    Dosen("Joko Susilo, Ph.D", "99887766", "Fakultas Hukum", "JS"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenScreen(dosenListViewModel: ListDosenViewModel = koinViewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    val listDosen = dosenListViewModel.dosenPaging.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Dosen", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Handle FAB click */ }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Dosen")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(searchQuery) { searchQuery = it }
            LazyColumn {
                item { ListHeader(listDosen.itemCount) }
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
            .padding(16.dp),
        placeholder = { Text("Cari nama atau NIDN...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun ListHeader(count: Int) {
    Text(
        text = "Jumlah Dosen ($count)",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun DosenListItem(initials: String, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle item click */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
//            Text(
//                text = "NIDN: ${dosen.nidn}",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//            Text(
//                text = dosen.faculty,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
//            )
        }

        IconButton(onClick = { /* TODO: Handle more options */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
        }
    }
}