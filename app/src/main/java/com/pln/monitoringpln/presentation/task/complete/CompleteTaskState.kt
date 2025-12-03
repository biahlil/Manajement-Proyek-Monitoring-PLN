package com.pln.monitoringpln.presentation.task.complete

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.User

data class CompleteTaskState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val task: Tugas? = null,
    val equipment: Alat? = null,
    val technician: User? = null,
    val condition: String = "",
    val equipmentStatus: String = "Normal",
    val proofUri: String? = null,
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false
)
