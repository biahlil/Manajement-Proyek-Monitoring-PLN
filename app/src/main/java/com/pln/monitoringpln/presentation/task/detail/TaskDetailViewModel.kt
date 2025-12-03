package com.pln.monitoringpln.presentation.task.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.usecase.tugas.DeleteTaskUseCase
import com.pln.monitoringpln.domain.usecase.tugas.GetTaskDetailUseCase
import com.pln.monitoringpln.domain.usecase.tugas.UpdateTaskStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val authRepository: AuthRepository,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Check Role
            val roleResult = authRepository.getUserRole()
            val role = roleResult.getOrDefault("technician")
            val isAdmin = role.equals("admin", ignoreCase = true)

            val result = getTaskDetailUseCase(taskId)
            if (result.isSuccess) {
                val detail = result.getOrNull()!!
                
                _state.update { 
                    it.copy(
                        isLoading = false,
                        task = detail.task,
                        equipment = detail.equipment,
                        technician = detail.technician,
                        isAdmin = isAdmin,
                        // Initialize Report Fields from Task
                        condition = detail.task.kondisiAkhir ?: "",
                        equipmentStatus = detail.equipment?.kondisi ?: "Normal",
                        taskStatus = detail.task.status,
                        proofUri = detail.task.buktiFoto
                    ) 
                }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Tugas tidak ditemukan") }
            }
        }
    }

    fun onDeleteTask() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            val taskId = _state.value.task?.id ?: return@launch
            val result = deleteTaskUseCase(taskId)
            if (result.isSuccess) {
                 _state.update { it.copy(showDeleteDialog = false, isDeleted = true) }
            } else {
                // Handle error (optional: show snackbar)
                 _state.update { it.copy(showDeleteDialog = false) }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }
}
