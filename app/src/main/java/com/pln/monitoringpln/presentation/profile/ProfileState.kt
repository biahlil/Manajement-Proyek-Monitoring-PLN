package com.pln.monitoringpln.presentation.profile

data class ProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val role: String = "",
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val area: String = "", // "Area Tugas" for Technician
    val isLoggedOut: Boolean = false,
    val photoUrl: String? = null
)
