package com.polije.sipeperpolije.feature.dashboard.data.model

import com.polije.sipeperpolije.feature.dashboard.domain.entity.ActivityAction
import com.polije.sipeperpolije.feature.dashboard.domain.entity.ActivityItemEntity
import com.polije.sipeperpolije.feature.dashboard.domain.entity.DashboardEntity
import com.polije.sipeperpolije.utils.asReadableString
import com.polije.sipeperpolije.utils.toRelativeTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlin.time.Instant

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

fun ActivityItemModel.toEntity(): ActivityItemEntity {
    val action = when (this.action) {
        "INSERT" -> ActivityAction.INSERT
        "UPDATE" -> ActivityAction.UPDATE
        "DELETE" -> ActivityAction.DELETE
        else -> ActivityAction.NONE
    }

    val normalizeTableName =
        tableName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    val title = when (action) {

        ActivityAction.INSERT -> "$normalizeTableName Baru Ditambahkan"
        ActivityAction.UPDATE -> "Update Data $normalizeTableName"
        ActivityAction.DELETE -> "Data $normalizeTableName Dihapus"
        ActivityAction.NONE -> normalizeTableName
    }


    val subtitle = when (action) {
        ActivityAction.INSERT -> dataNew?.getValue("nama")?.asReadableString() ?: ""
        ActivityAction.UPDATE ->
            when (changeColumn?.size) {
                1 -> "Mengubah data ${changeColumn.first().uppercase()}"
                else -> "Mengubah beberapa data"
            }


        ActivityAction.DELETE -> dataOld?.getValue("nama")?.asReadableString() ?: ""
        ActivityAction.NONE -> dataNew?.getValue("nama")?.asReadableString() ?: ""
    }

    val changeTime = changedAt.toRelativeTime()

    return ActivityItemEntity(
        id = id,
        action = action,
        title = title,
        subTitle = subtitle,
        changeTime = changeTime
    )

}

@Serializable
data class ActivityItemModel(
    val id: String,
    val action: String,
    @SerialName("changed_at")
    val changedAt: Instant,
    @SerialName("data_old")
    val dataOld: Map<String, JsonElement>? = null,
    @SerialName("data_new")
    val dataNew: Map<String, JsonElement>? = null,
    @SerialName("changed_columns")
    val changeColumn: List<String>?,
    @SerialName("table_name")
    val tableName: String
)

