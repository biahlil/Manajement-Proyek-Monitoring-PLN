package com.pln.monitoringpln.presentation.technician.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTechnicianViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddTechnicianState())
    val state: StateFlow<AddTechnicianState> = _state.asStateFlow()

    fun onNamaChange(value: String) {
        _state.update { it.copy(namaLengkap = value) }
    }

    fun onIdChange(value: String) {
        _state.update { it.copy(idTeknisi = value) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun onNoTeleponChange(value: String) {
        _state.update { it.copy(noTelepon = value) }
    }

    fun onAreaTugasChange(value: String) {
        _state.update { it.copy(areaTugas = value) }
    }

    fun onSaveTechnician() {
        viewModelScope.launch {
            val currentState = _state.value
            
            // Basic Validation
            if (currentState.namaLengkap.isBlank() || 
                currentState.idTeknisi.isBlank() || 
                currentState.email.isBlank() ||
                currentState.noTelepon.isBlank()) {
                _state.update { it.copy(error = "Semua field wajib diisi") }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }
            delay(1000) // Simulate save API call
            
            _state.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
