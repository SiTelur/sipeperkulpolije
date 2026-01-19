package com.polije.sipeperpolije.feature.dashboard.data.model

import com.polije.sipeperpolije.feature.dashboard.domain.entity.ActivityItemEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import kotlinx.serialization.Serializable

@Serializable
data class DashboardModel(
    val dosenActiveCount: Int,
    val matkulActiveCount: Int,
    val isLastGeneratedScheduleSuccess: Boolean,
    val recentActivity: List<ActivityItemModel>
)

fun DashboardModel.toEntity(): DashboardEntity = DashboardEntity(
    this.dosenActiveCount,
    this.matkulActiveCount,
    this.isLastGeneratedScheduleSuccess,
    this.recentActivity.map { it.toEntity() })

fun ActivityItemModel.toEntity(): ActivityItemEntity = ActivityItemEntity(this.title, this.subtitle)

@Serializable
data class ActivityItemModel(
    val title: String,
    val subtitle: String
)