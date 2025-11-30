package com.pln.monitoringpln.presentation.equipment.list

import com.pln.monitoringpln.domain.model.Alat

data class EquipmentListState(
    val isLoading: Boolean = false,
    val equipmentList: List<Alat> = emptyList(),
    val filteredEquipmentList: List<Alat> = emptyList(),
    val searchQuery: String = "",
    val isAdmin: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val equipmentToDelete: Alat? = null,
    val isDeleting: Boolean = false
)
