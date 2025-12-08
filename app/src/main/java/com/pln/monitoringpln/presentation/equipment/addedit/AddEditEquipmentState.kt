package com.pln.monitoringpln.presentation.equipment.addedit

data class AddEditEquipmentState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,

    // Form Fields
    val namaAlat: String = "",
    val kodeAlat: String = "",
    val tipePeralatan: String = "",
    val status: String = "Normal", // Normal, Rusak, Perlu Perhatian
    val lokasi: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // Mode
    val isEditMode: Boolean = false,
    val equipmentId: String? = null,
)
