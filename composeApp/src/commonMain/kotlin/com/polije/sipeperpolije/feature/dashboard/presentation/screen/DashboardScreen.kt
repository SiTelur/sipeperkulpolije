package com.polije.sipeperpolije.feature.dashboard.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.dashboard.presentation.component.ActivityItem
import com.polije.sipeperpolije.feature.dashboard.presentation.component.GenerateJadwalDialog
import com.polije.sipeperpolije.feature.dashboard.presentation.component.QuickActionButton
import com.polije.sipeperpolije.feature.dashboard.presentation.component.ShimmerActivityItem
import com.polije.sipeperpolije.feature.dashboard.presentation.component.SummaryCard
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardAction
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardEvent
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardLog
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardViewModel
import com.polije.sipeperpolije.theme.AppTheme
import com.polije.sipeperpolije.utils.ObserveAsEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel(), onGenerateJadwalPressed : () -> Unit, onLogout: () -> Unit) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarState = LocalSnackbarHostState.current
    var showGenerateJadwalDialog by remember { mutableStateOf(false) }


    ObserveAsEvent(viewModel.events) { event ->
        when (event) {
            is DashboardEvent.LogoutSuccess -> {
                snackBarState.showSnackbar("User Logout")
                onLogout()
            }

            is DashboardEvent.LogoutFailed -> {
                print(event.message)
            }

            is DashboardEvent.FetchDashboardFailed -> {
                snackBarState.showSnackbar(event.message)
            }

            is DashboardEvent.GenerateJadwalFailed -> {
                snackBarState.showSnackbar("Gagal ${event.message}")
            }

            DashboardEvent.GenerateJadwalSuccess -> {
                snackBarState.showSnackbar("Berhasil membuat jadwal")
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackBarState) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            HeaderSection(state.isLogoutLoading) { viewModel.onAction(DashboardAction.OnLogoutPressed) }

            SummaryStatisticsGrid(
                dosenCount = state.dosenCount,
                matkulCount = state.matkulCount,
                isLoading = state.isLoading
            )
            QuickActionButton {
                onGenerateJadwalPressed()
            }
            RecentActivitySection(
                isLoading = state.isLoading,
                items = state.list,
            )
        }


    }


}

@Composable
fun HeaderSection(isLogoutLoading: Boolean, onLogoutPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            )
            Column {
                Text(
                    text = "Halo, Admin",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.015).sp
                )
                Text(
                    text = "Senin, 23 Oktober 2023",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }


        IconButton(onClick = onLogoutPressed, enabled = !isLogoutLoading) {
            if (isLogoutLoading) CircularProgressIndicator(modifier = Modifier.padding(12.dp)) else Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }


    }
}

@Composable
fun SummaryStatisticsGrid(isLoading: Boolean, dosenCount: Int, matkulCount: Int) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                isLoading = isLoading,
                title = "Dosen Aktif",
                value = "$dosenCount",
                icon = Icons.Default.Group,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                iconColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                isLoading = isLoading,
                title = "Mata Kuliah",
                value = "$matkulCount",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconBgColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                iconColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
            )
        }
        ScheduleSummaryCard()
    }
}


@Composable
fun ScheduleSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Jadwal Kelas",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "120",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: Tergenerate",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Jadwal Kelas",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}


@Composable
fun RecentActivitySection(
    items: List<DashboardLog>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aktivitas Terbaru",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lihat Semua",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { }
            )
        }
        Spacer(Modifier.height(8.dp))
        when {
            isLoading -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(3) {
                        ShimmerActivityItem(isLoading, contentAfterLoading = {})
                    }
                }
            }

            items.isEmpty() -> {
                Text(
                    text = "Belum ada aktivitas",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(
                        items = items,
                        key = { it.id }
                    ) { item ->
                        ActivityItem(
                            icon = item.icon,
                            iconColor = MaterialTheme.colorScheme.primary,
                            title = item.title,
                            subtitle = item.subTitle
                        )
                    }
                }
            }
        }
    }
}


@Preview()
@Composable
fun SummaryCardPreview() {
    AppTheme {
        SummaryCard(
            title = "Dosen Aktif",
            value = "45",
            icon = Icons.Default.Group,
            iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            iconColor = MaterialTheme.colorScheme.primary,
        )
    }
}


