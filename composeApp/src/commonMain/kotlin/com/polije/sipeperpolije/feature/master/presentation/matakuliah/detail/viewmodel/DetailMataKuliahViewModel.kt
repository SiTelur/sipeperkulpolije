package com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.sipeperpolije.feature.master.domain.usecase.DeleteMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.GetDetailMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.SearchDosenUseCase
import com.polije.sipeperpolije.feature.master.domain.usecase.UpdateMataKuliahUseCase
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.toUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahEvent.OnFailureDeleteMataKuliah
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahEvent.OnFailureUpdateMataKuliah
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahEvent.OnSuccessDeleteMataKuliah
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.viewmodel.DetailMataKuliahEvent.OnSuccessUpdateMataKuliah
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.MataKuliahUI
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.viewmodel.toUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailMataKuliahViewModel(
    private val getDetailMataKuliahUseCase: GetDetailMataKuliahUseCase,
    private val searchDosenUseCase: SearchDosenUseCase,
    private val deleteMataKuliahUseCase: DeleteMataKuliahUseCase,
    private val updateMataKuliahUseCase: UpdateMataKuliahUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailMataKuliahState(detail = null))
    val state = _state.asStateFlow()

    private val _events = Channel<DetailMataKuliahEvent>()
    val events = _events.receiveAsFlow()

    init {
        initiateDosenList()
    }

    private fun initiateDosenList() {
        viewModelScope.launch {
            searchDosenUseCase().onSuccess { response ->
                _state.update { it.copy(result = response.map { value -> value.toUI() }) }
            }
        }
    }

    fun onAction(action: DetailMataKuliahAction) {
        when (action) {
            is DetailMataKuliahAction.OnDetailMataKuliahUpdate -> {
                viewModelScope.launch {
                    updateMataKuliahUseCase(
                        MataKuliahUI(
                            id = action.id,
                            kode = action.kode,
                            nama = action.nama,
                            semester = action.semester,
                            sksTeori = action.sksTeori,
                            sksPraktek = action.sksPraktek,
                            isActive = action.isActive,
                            idPengampu = action.idDosen,
                            namaPenampu = ""
                        )
                    ).onSuccess {
                        _events.send(OnSuccessUpdateMataKuliah)
                    }.onFailure {
                        _events.send(OnFailureUpdateMataKuliah(it.message.toString()))
                    }
                }
            }

            is DetailMataKuliahAction.OnDetailMataKuliahDelete -> {
                viewModelScope.launch {
                    deleteMataKuliahUseCase(id = action.id).onSuccess {
                        _events.send(OnSuccessDeleteMataKuliah)
                    }.onFailure {
                        _events.send(OnFailureDeleteMataKuliah(it.message.toString()))
                    }
                }
            }

            is DetailMataKuliahAction.OnDetailMataKuliahLoad -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = false) }
                    getDetailMataKuliahUseCase(action.id)
                        .onSuccess { value ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    detail = value.toUI()
                                )
                            }
                        }.onFailure {
                            _events.send(OnFailureUpdateMataKuliah(it.message.toString()))
                        }
                }
            }
        }

    }
}