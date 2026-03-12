package com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.core.Semester
import com.polije.sipeperpolije.feature.dashboard.domain.entity.toUI
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.GenerateJadwalUseCase
import com.polije.sipeperpolije.feature.dashboard.domain.usecase.PreviewJadwalUseCase
import com.polije.sipeperpolije.feature.dashboard.presentation.dashboard.viewmodel.SelectSemester
import com.polije.sipeperpolije.feature.dashboard.presentation.generate.viewmodel.GenerateJadwalEvent.PreviewJadwalFailed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenerateJadwalViewModel(
    private val previewJadwalUseCase: PreviewJadwalUseCase,
    private val generateJadwalUseCase: GenerateJadwalUseCase
) :
    ViewModel() {

    private val _state = MutableStateFlow(GenerateJadwalState())
    val state = _state.asStateFlow()

    private val _events = Channel<GenerateJadwalEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: GenerateJadwalAction) {
        when (action) {
            is GenerateJadwalAction.OnPreviewJadwal -> {
                _state.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    val actualSemester = when (action.semester) {
                        SelectSemester.Ganjil -> Semester.GANJIL
                        SelectSemester.Genap -> Semester.GENAP
                    }

                    previewJadwalUseCase(actualSemester).onSuccess { preview ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                list = preview.map { value -> value.toUI() })
                        }
                    }.onFailure { throwable ->
                        _state.update { it.copy(isLoading = false) }
                        _events.send(
                            PreviewJadwalFailed(
                                throwable.message ?: "Unknown error"
                            )
                        )
                    }
                }
            }

            is GenerateJadwalAction.OnGenerateJadwal -> {
                val actualSemester = when (action.semester) {
                    SelectSemester.Ganjil -> Semester.GANJIL
                    SelectSemester.Genap -> Semester.GENAP
                }

                viewModelScope.launch {
                    generateJadwalUseCase(
                        title = action.title,
                        actualSemester,
                        overrideJamPraktek = action.overrideJamPraktikum
                    ).onSuccess {

                    }.onFailure {
                        
                    }
                }
            }
        }
    }

}