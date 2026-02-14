package com.polije.sipeperpolije.feature.dashboard.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.polije.sipeperpolije.feature.DashboardRoute

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    NavItem(
        "Dashboard",
        Icons.Outlined.Dashboard,
        Icons.Filled.Dashboard,
        DashboardRoute.Dashboard.route
    ),
    NavItem("Dosen", Icons.Outlined.School, Icons.Filled.School, DashboardRoute.Dosen.route),
    NavItem(
        "Matkul",
        Icons.AutoMirrored.Outlined.MenuBook,
        Icons.AutoMirrored.Filled.MenuBook,
        DashboardRoute.MataKuliah.route
    ),
    NavItem(
        "Jadwal",
        Icons.Outlined.CalendarToday,
        Icons.Filled.CalendarToday,
        DashboardRoute.Jadwal.route
    ),
    NavItem(
        "Settings",
        Icons.Outlined.Settings,
        Icons.Filled.Settings,
        DashboardRoute.Settings.route
    )
)
