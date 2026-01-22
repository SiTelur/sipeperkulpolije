package com.polije.sipeperpolije.feature.dashboard.domain.entity

data class DashboardEntity(
    val dosenActiveCount: Int,
    val matkulActiveCount: Int,
    val isLastGeneratedScheduleSuccess: Boolean,
    val recentActivities: List<ActivityItemEntity>
)

data class ActivityItemEntity(
    val id: String,
    val action: ActivityAction,
    val title: String,
    val subTitle: String,
    val changeTime: String
)


enum class ActivityAction {
    INSERT, UPDATE, DELETE, NONE
}