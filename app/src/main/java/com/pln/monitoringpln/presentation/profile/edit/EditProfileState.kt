package com.pln.monitoringpln.presentation.profile.edit

data class EditProfileState(
    val name: String = "",
    val id: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val role: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
)
