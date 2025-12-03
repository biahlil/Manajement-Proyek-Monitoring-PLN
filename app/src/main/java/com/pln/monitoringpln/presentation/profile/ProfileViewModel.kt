package com.pln.monitoringpln.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.pln.monitoringpln.domain.repository.UserRepository

class ProfileViewModel(
    private val getUserProfileUseCase: com.pln.monitoringpln.domain.usecase.user.GetUserProfileUseCase,
    private val logoutUseCase: com.pln.monitoringpln.domain.usecase.auth.LogoutUseCase,
    private val observeUserProfileUseCase: com.pln.monitoringpln.domain.usecase.user.ObserveUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            observeUserProfileUseCase().collect { user ->
                if (user != null) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            name = user.namaLengkap,
                            role = user.role.lowercase().replaceFirstChar { it.uppercase() },
                            id = user.id,
                            username = user.email.substringBefore("@"),
                            email = user.email,
                            photoUrl = user.photoUrl,
                            phone = "-", // Phone not in User model yet
                            area = if (user.role.equals("admin", ignoreCase = true)) "-" else "Area Tugas"
                        )
                    }
                }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getUserProfileUseCase()
            val user = result.getOrNull()

            if (user != null) {
                // Initial load handled by observation mostly, but this ensures fetch if needed
            } else {
                 _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.update { it.copy(isLoggedOut = true) }
        }
    }
}
