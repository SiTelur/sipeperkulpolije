package com.polije.sipeperpolije.feature.master

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class MataKuliahDetails(
    val name: String,
    val code: String,
    val sks: Int,
    val type: String,
    val lecturer: String,
    val schedule: String,
    val room: String,
    val description: String
)

val sampleMataKuliahDetail = MataKuliahDetails(
    name = "Algoritma & Struktur Data",
    code = "IF-2024",
    sks = 3,
    type = "Wajib",
    lecturer = "Dr. Budi Santoso, M.Kom",
    schedule = "Senin, 08:00 - 10:30 WIB",
    room = "Gedung A, Lab. Komputer 3",
    description = "Mata kuliah ini mempelajari konsep dasar algoritma dan struktur data, termasuk array, linked list, stack, queue, tree, dan graph. Mahasiswa akan belajar menganalisis kompleksitas waktu dan ruang dari algoritma yang dibuat serta penerapannya dalam penyelesaian masalah pemrograman yang efisien."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MataKuliahDetailScreen() {
    val matkul = sampleMataKuliahDetail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Mata Kuliah", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            ) {
                Button(
                    onClick = { /* TODO: Handle edit information */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Informasi", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item { HeroSection(matkul) }
            item { QuickStats(matkul) }
            item { InfoSection(matkul) }

        }
    }
}

@Composable
private fun HeroSection(matkul: MataKuliahDetails) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .height(192.dp)
            .fillMaxWidth()
    ) {
        // Placeholder for the background image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = matkul.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun QuickStats(matkul: MataKuliahDetails) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), label = "Kode", value = matkul.code, icon = Icons.Default.Fingerprint)
        StatCard(modifier = Modifier.weight(1f), label = "Kredit", value = "${matkul.sks} SKS", icon = Icons.Default.School)
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String, icon: ImageVector) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun InfoSection(matkul: MataKuliahDetails) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(
            icon = Icons.Default.Person,
            iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            iconColor = MaterialTheme.colorScheme.primary,
            label = "Dosen Pengampu",
            value = matkul.lecturer
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ChatBubble, contentDescription = "Chat Dosen", tint = MaterialTheme.colorScheme.primary)
            }
        }
        InfoRow(
            icon = Icons.Default.Schedule,
            iconBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconColor = MaterialTheme.colorScheme.onTertiaryContainer,
            label = "Jadwal Kuliah",
            value = matkul.schedule
        )
        InfoRow(
            icon = Icons.Default.LocationOn,
            iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            label = "Ruangan",
            value = matkul.room
        ) {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) { /* Placeholder for map */ }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconColor: Color,
    label: String,
    value: String,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp).weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}