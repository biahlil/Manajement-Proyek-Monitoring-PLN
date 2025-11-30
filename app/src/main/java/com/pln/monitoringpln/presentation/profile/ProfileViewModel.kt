package com.pln.monitoringpln.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Check User Role
            val roleResult = authRepository.getUserRole()
            val role = roleResult.getOrDefault("technician") // Default to technician for now
            val isAdmin = role == "admin"

            // Mock Data based on Role
            val user = if (isAdmin) {
                com.pln.monitoringpln.domain.model.User(
                    id = "ADM-001",
                    email = "admin@pln.co.id",
                    namaLengkap = "Admin PLN",
                    role = "admin"
                )
            } else {
                com.pln.monitoringpln.domain.model.User(
                    id = "TKN-BJM-001-PLN",
                    email = "rusmanhadi@gmail.com",
                    namaLengkap = "Rusman Hadi",
                    role = "technician"
                )
            }

            _state.update { 
                it.copy(
                    isLoading = false,
                    name = user.namaLengkap,
                    role = if (user.role == "admin") "Administrator" else "Teknisi",
                    id = user.id,
                    username = user.email.substringBefore("@"), // Mock username from email
                    email = user.email,
                    phone = if (isAdmin) "0812-3456-7890" else "+62 849 - 1328 - 7124",
                    area = if (isAdmin) "-" else "Gardu Kayu Tangi 1, Gardu Kayu Tangi 2"
                )
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update { it.copy(isLoggedOut = true) }
        }
    }
}
