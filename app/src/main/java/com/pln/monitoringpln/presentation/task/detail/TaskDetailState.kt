package com.pln.monitoringpln.presentation.task.detail

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User

data class TaskDetailState(
    val isLoading: Boolean = false,
    val task: Tugas? = null,
    val equipment: Alat? = null,
    val technician: User? = null,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleted: Boolean = false,
    val isAdmin: Boolean = false,

    // Report Fields
    val condition: String = "",
    val equipmentStatus: String = "Normal", // Normal, Rusak, Perlu Perhatian
    val proofUri: String? = null,
    val taskStatus: String = "To Do", // To Do, In Progress, Finish
    val isReportSaved: Boolean = false,
    val isSavingReport: Boolean = false,
)
