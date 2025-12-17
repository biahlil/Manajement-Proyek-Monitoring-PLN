package com.pln.monitoringpln.presentation.technician.add

data class AddTechnicianState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,

    // Form Fields
    val namaLengkap: String = "",
    val namaError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val photoUri: android.net.Uri? = null,
)
