package com.pln.monitoringpln.presentation.technician.add

data class AddTechnicianState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    
    // Form Fields
    val namaLengkap: String = "",
    val idTeknisi: String = "",
    val email: String = "",
    val noTelepon: String = "",
    val areaTugas: String = ""
)
