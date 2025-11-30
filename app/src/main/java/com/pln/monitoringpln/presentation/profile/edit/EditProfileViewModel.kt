package com.pln.monitoringpln.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        // Mock data loading
        _state.update { 
            it.copy(
                name = "Admin PLN",
                id = "ADM-001",
                email = "admin@pln.co.id",
                phone = "081234567890",
                isLoading = false
            ) 
        }
    }

    fun onNameChange(newValue: String) {
        _state.update { it.copy(name = newValue) }
    }

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPhoneChange(newValue: String) {
        _state.update { it.copy(phone = newValue) }
    }

    fun onSave() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500) // Simulate network call
            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
