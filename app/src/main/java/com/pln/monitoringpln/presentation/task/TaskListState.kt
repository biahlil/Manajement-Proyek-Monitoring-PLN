package com.pln.monitoringpln.presentation.task

import com.pln.monitoringpln.domain.model.Tugas

data class TaskListState(
    val isLoading: Boolean = false,
    val tasks: List<Tugas> = emptyList(),
    val filteredTasks: List<Tugas> = emptyList(),
    val searchQuery: String = "",
    val isAdmin: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val taskToDelete: Tugas? = null,
    val technicianNames: Map<String, String> = emptyMap(),
    val equipmentNames: Map<String, String> = emptyMap()
)
