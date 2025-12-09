package com.pln.monitoringpln.presentation.equipment.addedit

data class AddEditEquipmentState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val savedCondition: String? = null,
    val error: String? = null,

    // Form Fields
    val namaAlat: String = "",
    val namaAlatError: String? = null,
    val tipeError: String? = null,
    val kodeAlat: String = "",
    val tipePeralatan: String = "",
    val description: String = "", // Description (Kondisi)
    val status: String = "Normal", // Normal, Rusak, Perlu Perhatian
    val lokasi: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // Mode
    val isEditMode: Boolean = false,
    val equipmentId: String? = null,
)
