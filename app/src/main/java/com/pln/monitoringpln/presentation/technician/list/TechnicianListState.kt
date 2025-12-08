package com.pln.monitoringpln.presentation.technician.list

import com.pln.monitoringpln.domain.model.User

data class TechnicianListState(
    val isLoading: Boolean = false,
    val technicians: List<User> = emptyList(),
    val filteredTechnicians: List<User> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val technicianToDelete: User? = null,
    val isDeleting: Boolean = false,
    val technicianTaskCounts: Map<String, Int> = emptyMap(),
    val technicianEquipment: Map<String, String> = emptyMap(), // idTeknisi -> Nama Alat
)
