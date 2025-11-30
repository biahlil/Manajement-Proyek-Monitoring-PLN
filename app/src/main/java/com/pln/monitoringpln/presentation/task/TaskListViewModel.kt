package com.pln.monitoringpln.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Check User Role
            val roleResult = authRepository.getUserRole()
            val role = roleResult.getOrDefault("technician")
            val isAdmin = role == "admin"

            // Mock Data
            // Mock Data
            val allTasks = listOf(
                Tugas(id = "1", deskripsi = "Inspeksi Gardu A", idAlat = "GRD-A", idTeknisi = "1", status = "In Progress", tglJatuhTempo = java.util.Date()),
                Tugas(id = "2", deskripsi = "Perbaikan Trafo B", idAlat = "GRD-B", idTeknisi = "1", status = "To Do", tglJatuhTempo = java.util.Date()),
                Tugas(id = "3", deskripsi = "Pengecekan Kabel C", idAlat = "GRD-C", idTeknisi = "3", status = "Done", tglJatuhTempo = java.util.Date()),
                Tugas(id = "4", deskripsi = "Ganti Oli Trafo D", idAlat = "GRD-D", idTeknisi = "2", status = "In Progress", tglJatuhTempo = java.util.Date()),
                Tugas(id = "5", deskripsi = "Inspeksi Rutin E", idAlat = "GRD-E", idTeknisi = "1", status = "To Do", tglJatuhTempo = java.util.Date())
            )

            val tasks = if (isAdmin) {
                allTasks
            } else {
            } else {
                allTasks.filter { it.idTeknisi == "1" } // Mock filter for current user (ID: 1)
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    tasks = tasks,
                    filteredTasks = tasks,
                    isAdmin = isAdmin
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { state ->
            val filtered = if (query.isBlank()) {
                state.tasks
            } else {
                state.tasks.filter {
                    it.deskripsi.contains(query, ignoreCase = true) ||
                    it.idAlat.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredTasks = filtered)
        }
    }

    fun onDeleteTask(task: Tugas) {
        _state.update { it.copy(showDeleteConfirmation = true, taskToDelete = task) }
    }

    fun onConfirmDelete() {
        val taskToDelete = _state.value.taskToDelete
        if (taskToDelete != null) {
            // Mock Delete
            val updatedTasks = _state.value.tasks.filter { it.id != taskToDelete.id }
            val updatedFiltered = _state.value.filteredTasks.filter { it.id != taskToDelete.id }
            
            _state.update { 
                it.copy(
                    tasks = updatedTasks,
                    filteredTasks = updatedFiltered,
                    showDeleteConfirmation = false,
                    taskToDelete = null
                )
            }
        }
    }

    fun onDismissDelete() {
        _state.update { it.copy(showDeleteConfirmation = false, taskToDelete = null) }
    }
}
