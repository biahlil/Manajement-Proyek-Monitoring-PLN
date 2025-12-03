package com.pln.monitoringpln.presentation.equipment.detail

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.AlatHistory

data class EquipmentDetailState(
    val isLoading: Boolean = false,
    val alatHistory: AlatHistory? = null,
    val isAdmin: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)
