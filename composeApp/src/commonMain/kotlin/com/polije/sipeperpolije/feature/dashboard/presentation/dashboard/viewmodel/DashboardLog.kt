package com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import com.polije.sipeperpolije.feature.dashboard.domain.entity.ActivityAction
import com.polije.sipeperpolije.feature.dashboard.domain.entity.ActivityItemEntity

data class DashboardLog(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val subTitle: String
)

fun ActivityItemEntity.toUI(): DashboardLog {
    val icon = when (this.action) {
        ActivityAction.INSERT -> Icons.Default.PersonAdd
        ActivityAction.UPDATE -> Icons.Outlined.Edit
        ActivityAction.DELETE -> Icons.Outlined.Delete
        ActivityAction.NONE -> Icons.Default.PersonAdd

    }
    return DashboardLog(this.id, icon, this.title, "${this.subTitle} • ${this.changeTime}")
}