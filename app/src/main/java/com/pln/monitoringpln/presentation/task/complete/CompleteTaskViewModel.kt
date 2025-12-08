package com.pln.monitoringpln.presentation.task.complete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.usecase.tugas.CompleteTaskUseCase
import com.pln.monitoringpln.domain.usecase.tugas.GetTaskDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompleteTaskViewModel(
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CompleteTaskState())
    val state: StateFlow<CompleteTaskState> = _state.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = getTaskDetailUseCase(taskId)
            result.onSuccess { (task, equipment, technician) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        task = task,
                        equipment = equipment,
                        technician = technician,
                        condition = task.kondisiAkhir ?: "",
                        equipmentStatus = equipment?.kondisi ?: "Normal",
                        proofUri = task.buktiFoto,
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun onConditionChange(condition: String) {
        _state.update { it.copy(condition = condition) }
    }

    fun onEquipmentStatusChange(status: String) {
        _state.update { it.copy(equipmentStatus = status) }
    }

    fun onProofSelected(uri: String) {
        _state.update { it.copy(proofUri = uri) }
    }

    fun onCompleteTask(photoBytes: ByteArray?) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val currentState = _state.value
            val task = currentState.task ?: return@launch

            if (photoBytes == null && currentState.proofUri.isNullOrBlank()) {
                _state.update { it.copy(isSaving = false, error = "Foto bukti wajib diupload") }
                return@launch
            }

            if (currentState.condition.isBlank()) {
                _state.update { it.copy(isSaving = false, error = "Kondisi alat wajib diisi") }
                return@launch
            }

            val result = completeTaskUseCase(
                taskId = task.id,
                photoBytes = photoBytes,
                newCondition = currentState.condition,
                currentProofUrl = if (photoBytes == null) currentState.proofUri else null,
            )

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isCompleted = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Gagal menyimpan laporan") }
            }
        }
    }
}
