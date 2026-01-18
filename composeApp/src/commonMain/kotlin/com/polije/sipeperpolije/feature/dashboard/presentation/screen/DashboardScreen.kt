package com.polije.sipeperpolije.feature.dashboard.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardEvent
import com.polije.sipeperpolije.feature.dashboard.presentation.viewmodel.DashboardViewModel
import com.polije.sipeperpolije.feature.login.presentation.viewmodel.LoginAction
import com.polije.sipeperpolije.feature.master.DosenScreen
import com.polije.sipeperpolije.feature.master.JadwalScreen
import com.polije.sipeperpolije.feature.master.MataKuliahScreen
import com.polije.sipeperpolije.feature.master.SettingsScreen
import com.polije.sipeperpolije.utils.ObserveAsEvent
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var selectedNavigationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    ObserveAsEvent(viewModel.events) { event ->
        when (event) {
            is DashboardEvent.LogoutSuccess -> {
                onLogout()
            }

            is DashboardEvent.LogoutFailed -> {
                print(event.message)
            }
        }
    }


    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            navigationItems.forEachIndexed { index, item ->
                item(
                    selected = selectedNavigationIndex == index,
                    onClick = {
                        selectedNavigationIndex = index
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (item.route == currentRoute) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                        )
                    },
                    label = { Text(item.label) }
                )
            }
        }
    ) {
        val graph = navController.createGraph(startDestination = DashboardRoute.Dashboard.route) {
            composable(DashboardRoute.Dashboard.route) {
                DashboardContent { viewModel.onAction(LoginAction.OnLoginPressed) }
            }

            composable(DashboardRoute.Dosen.route) {
                DosenScreen()
            }

            composable(DashboardRoute.MataKuliah.route) {
                MataKuliahScreen()
            }

            composable(DashboardRoute.Jadwal.route) {
                JadwalScreen()
            }

            composable(DashboardRoute.Settings.route) {
                SettingsScreen()
            }
        }

        NavHost(navController = navController, graph = graph)
    }
}

@Composable
fun DashboardContent(onLogoutPressed: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp)
    ) {
        item { HeaderSection(true, onLogoutPressed) }
        item { SummaryStatisticsGrid() }
        item { QuickActionButton() }
        item { RecentActivitySection() }
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
fun SummaryStatisticsGrid() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                title = "Dosen Aktif",
                value = "45",
                icon = Icons.Default.Group,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                iconColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            SummaryCard(
                title = "Mata Kuliah",
                value = "32",
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
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
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
fun QuickActionButton() {
    OutlinedButton(
        onClick = { /* TODO */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            brush = Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        ),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Generate Jadwal Baru",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecentActivitySection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ActivityItem(
                icon = Icons.Default.CheckCircle,
                iconColor = MaterialTheme.colorScheme.tertiary,
                title = "Jadwal T. Informatika Fix",
                subtitle = "Berhasil digenerate • 2 jam yang lalu",
            )
            ActivityItem(
                icon = Icons.Default.PersonAdd,
                iconColor = MaterialTheme.colorScheme.primary,
                title = "Dosen Baru Ditambahkan",
                subtitle = "Budi Santoso, M.Kom • 5 jam yang lalu",
            )
            ActivityItem(
                icon = Icons.Outlined.Edit,
                iconColor = MaterialTheme.colorScheme.secondary,
                title = "Update Mata Kuliah",
                subtitle = "Algoritma Pemrograman • Kemarin",
            )
        }
    }
}

@Composable
fun ActivityItem(icon: ImageVector, iconColor: Color, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}


sealed class DashboardRoute(val route: String) {
    object Dashboard : DashboardRoute("dashboard")
    object Dosen : DashboardRoute("dosen")
    object MataKuliah : DashboardRoute("matkul")
    object Jadwal : DashboardRoute("jadwal")
    object Settings : DashboardRoute("settings")
}
