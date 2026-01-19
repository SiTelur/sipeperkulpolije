package com.polije.sipeperpolije.feature.dashboard.domain.entity

data class DashboardEntity(
    val dosenActiveCount: Int,
    val matkulActiveCount: Int,
    val isLastGeneratedScheduleSuccess: Boolean,
    val recentActivities: List<ActivityItemEntity>
)

data class ActivityItemEntity(
    val title: String,
    val subTitle: String
)