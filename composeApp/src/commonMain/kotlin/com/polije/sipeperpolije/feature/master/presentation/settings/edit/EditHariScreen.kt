package com.polije.sipeperpolije.feature.master.presentation.settings.edit

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.polije.sipeperpolije.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHariScreen(
    onBackButtonPressed: () -> Unit
) {
    val days = remember {
        mutableStateOf(
            listOf(
                Day("Monday", "M", "09:00", "17:00", true),
                Day("Tuesday", "T", "09:00", "17:00", true),
                Day("Wednesday", "W", "09:00", "17:00", true),
                Day("Thursday", "T", "09:00", "17:00", true),
                Day("Friday", "F", "09:00", "16:00", true),
                Day("Saturday", "S", "", "", false),
                Day("Sunday", "S", "", "", false)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Availability", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackButtonPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Set your weekly schedule",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Configure your active working days and hours for the upcoming week.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(days.value) {
                DayItem(it)
            }
            item {
                Spacer(Modifier.height(80.dp)) // Spacer for bottom bar
            }
        }
    }
}

@Composable
private fun DayItem(day: Day) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.1f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        day.initial,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    day.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = if (day.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (day.isActive) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.padding(start = 0.dp), // No padding start as per design
                    verticalAlignment = Alignment.Bottom
                ) {
                    TimePicker(
                        label = "Start Time",
                        time = day.startTime,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "To",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TimePicker(
                        label = "End Time",
                        time = day.endTime,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { /*TODO*/ },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            } else {
                Text(
                    "Unavailable",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 44.dp, top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TimePicker(label: String, time: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = time,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(Icons.Outlined.Schedule, contentDescription = null)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            singleLine = true
        )
    }
}

data class Day(
    val name: String,
    val initial: String,
    val startTime: String,
    val endTime: String,
    val isActive: Boolean
)

@Preview
@Composable
fun EditHariScreenPreview() {
    AppTheme { EditHariScreen({}) }
}


