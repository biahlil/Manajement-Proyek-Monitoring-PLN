package com.pln.monitoringpln.presentation.equipment.detail

import com.pln.monitoringpln.domain.model.Alat

data class EquipmentDetailState(
    val isLoading: Boolean = false,
    val equipment: Alat? = null,
    val isAdmin: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)
