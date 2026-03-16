package com.polije.sipeperpolije.feature.dashboard.presentation.generate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.polije.sipeperpolije.LocalSnackbarHostState
import com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel.SelectSemester
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.GenerateJadwalAction
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.GenerateJadwalEvent
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.GenerateJadwalViewModel
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.PreviewJadwalUI
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.PreviewJadwalUIItem
import com.polije.sipeperpolije.theme.AppTheme
import com.polije.sipeperpolije.utils.ObserveAsEvent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateJadwalScreen(
    generateJadwalViewModel: GenerateJadwalViewModel = koinViewModel(),
    onBackPressed: () -> Unit
) {

    val state by generateJadwalViewModel.state.collectAsState()
    val snackBarState = LocalSnackbarHostState.current

    ObserveAsEvent(generateJadwalViewModel.events) { event ->
        when (event) {
            is GenerateJadwalEvent.PreviewJadwalFailed -> {
                snackBarState.showSnackbar(event.message)
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        "Generate Jadwal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWideScreen = maxWidth > 800.dp

            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Left side: Config Form
                    Column(modifier = Modifier.weight(1f)) {
                        GenerateForm(onGeneratePressed = { title, semester, override ->
                            generateJadwalViewModel.onAction(
                                GenerateJadwalAction.OnGenerateJadwal(
                                    title,
                                    semester,
                                    override
                                )
                            )
                        }, onPreviewPressed = { semester ->
                            generateJadwalViewModel.onAction(
                                GenerateJadwalAction.OnPreviewJadwal(
                                    semester
                                )
                            )
                        })
                    }

                    // Right side: Preview
                    Column(modifier = Modifier.weight(1.5f)) {
                        PreviewHeader()
                        Spacer(Modifier.height(16.dp))
                        PreviewList(modifier = Modifier.weight(1f), listPreview = state.list)
                    }
                }
            } else {
                // Mobile: Vertical Scroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    GenerateForm(onGeneratePressed = { title, semester, override ->
                        generateJadwalViewModel.onAction(
                            GenerateJadwalAction.OnGenerateJadwal(
                                title,
                                semester,
                                override
                            )
                        )
                    }, onPreviewPressed = { semester ->
                        generateJadwalViewModel.onAction(
                            GenerateJadwalAction.OnPreviewJadwal(
                                semester
                            )
                        )
                    })
                    Spacer(Modifier.height(32.dp))
                    PreviewHeader()
                    Spacer(Modifier.height(16.dp))
                    PreviewList(modifier = Modifier.weight(1f), listPreview = listOf())
                }
            }
        }
    }
}

@Composable
fun GenerateForm(
    onPreviewPressed: (SelectSemester) -> Unit,
    onGeneratePressed: (String, SelectSemester, Int?) -> Unit
) {
    val (semester, setSemester) = remember { mutableStateOf(SelectSemester.Ganjil) }
    val namaJadwalTextState = rememberTextFieldState()
    val overrideJamPraktikumTextState = rememberTextFieldState()
    var overridePraktikum by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            namaJadwalTextState.text.isNotEmpty()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column {
            Text(
                "Nama Jadwal",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = namaJadwalTextState,
            )
        }
        DropdownField<SelectSemester, String>(
            label = "Semester",
            value = semester.name,
            options = SelectSemester.entries.toList(),
            onSelected = { setSemester(it) },
        )


        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            onClick = { overridePraktikum = !overridePraktikum }
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        Text(
                            "Override Jam Praktikum",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Isi dengan format jam untuk mengubah jam praktikum",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Checkbox(
                        checked = overridePraktikum,
                        onCheckedChange = { overridePraktikum = it })
                }

                AnimatedVisibility(visible = overridePraktikum) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = overrideJamPraktikumTextState,
                        inputTransformation = InputTransformation {
                            val text = asCharSequence().filter { it.isDigit() }.take(1)
                            replace(0, length, text)
                        })
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { onPreviewPressed(semester) },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Outlined.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("PREVIEW", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Button(
                onClick = {
                    onGeneratePressed(
                        namaJadwalTextState.text.toString(),
                        semester,
                        overrideJamPraktikumTextState.text.toString().toIntOrNull()
                    )
                },
                enabled = isFormValid,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(
                    Icons.Default.Autorenew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("GENERATE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun PreviewHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Preview Input Jadwal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PreviewList(listPreview: List<PreviewJadwalUI>, modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listPreview.forEach { preview ->
            item { Text(preview.nama, fontWeight = FontWeight.Bold) }
            items(preview.list) {
                PreviewCard(it)
            }
        }
    }
}

@Composable
fun PreviewCard(item: PreviewJadwalUIItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(
                alpha = 0.2f
            )
        )
    ) {
        Box {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            item.name,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            item.desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun <S, T> DropdownField(
    label: String,
    value: String,
    options: List<S>,
    onSelected: (S) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Box {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                onClick = { expanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(Icons.Outlined.ExpandMore, contentDescription = null)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Preview(device = "spec:width=1000dp,height=841dp,dpi=420")
@Composable
fun GenerateJadwalScreenPreview() {
    AppTheme {


        GenerateJadwalScreen() {}
    }
}
