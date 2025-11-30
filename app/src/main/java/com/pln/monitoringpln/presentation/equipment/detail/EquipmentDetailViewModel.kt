package com.pln.monitoringpln.presentation.equipment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EquipmentDetailViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EquipmentDetailState())
    val state: StateFlow<EquipmentDetailState> = _state.asStateFlow()

    // Mock Data (Same as List for consistency)
    private val allEquipments = listOf(
        Alat(id = "1", kodeAlat = "TRF-001", namaAlat = "Trafo Kayu Tangi 1", latitude = -3.3194, longitude = 114.5908, kondisi = "Normal"),
        Alat(id = "2", kodeAlat = "TRF-002", namaAlat = "Trafo Kayu Tangi 2", latitude = -3.3200, longitude = 114.5910, kondisi = "Normal"),
        Alat(id = "3", kodeAlat = "CBL-001", namaAlat = "Kabel Bawah Tanah", latitude = -3.3210, longitude = 114.5920, kondisi = "Rusak"),
        Alat(id = "4", kodeAlat = "PNL-001", namaAlat = "Panel Distribusi", latitude = -3.3220, longitude = 114.5930, kondisi = "Perlu Perhatian"),
        Alat(id = "5", kodeAlat = "TRF-003", namaAlat = "Trafo C", latitude = -3.3230, longitude = 114.5940, kondisi = "Normal")
    )

    fun loadEquipment(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500) // Simulate network

            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull() == "admin"
            val equipment = allEquipments.find { it.id == id }

            if (equipment != null) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        equipment = equipment, 
                        isAdmin = isAdmin 
                    ) 
                }
            } else {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Alat tidak ditemukan" 
                    ) 
                }
            }
        }
    }

    fun onDeleteClick() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            delay(1000) // Simulate delete
            _state.update { 
                it.copy(
                    isDeleting = false, 
                    showDeleteDialog = false, 
                    isDeleted = true 
                ) 
            }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }
}
