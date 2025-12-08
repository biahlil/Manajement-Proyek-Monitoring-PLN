package com.pln.monitoringpln.presentation.task.addedit

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.User
import java.time.LocalDate

data class AddEditTaskState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isTaskSaved: Boolean = false,
    val taskId: String? = null,

    // Form Fields
    val title: String = "",
    val description: String = "", // Formerly Catatan Tambahan
    val selectedEquipment: Alat? = null,
    val deadline: LocalDate? = null, // Formerly Jadwal
    val selectedTechnician: User? = null,

    // Dropdown Data
    val availableEquipments: List<Alat> = emptyList(),
    val availableTechnicians: List<User> = emptyList(),

    // Search Query for Equipment
    val equipmentSearchQuery: String = "",

    val savedTaskId: String? = null,
    // Status (for Edit)
    val status: String = "To Do",
)
