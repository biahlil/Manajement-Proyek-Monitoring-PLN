package com.pln.monitoringpln.presentation.equipment.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditEquipmentViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddEditEquipmentState())
    val state: StateFlow<AddEditEquipmentState> = _state.asStateFlow()

    // Mock Data (Same as List for consistency)
    private val allEquipments = listOf(
        Alat(id = "1", kodeAlat = "TRF-001", namaAlat = "Trafo Kayu Tangi 1", latitude = -3.3194, longitude = 114.5908, kondisi = "Normal"),
        Alat(id = "2", kodeAlat = "TRF-002", namaAlat = "Trafo Kayu Tangi 2", latitude = -3.3200, longitude = 114.5910, kondisi = "Normal"),
        Alat(id = "3", kodeAlat = "CBL-001", namaAlat = "Kabel Bawah Tanah", latitude = -3.3210, longitude = 114.5920, kondisi = "Rusak"),
        Alat(id = "4", kodeAlat = "PNL-001", namaAlat = "Panel Distribusi", latitude = -3.3220, longitude = 114.5930, kondisi = "Perlu Perhatian"),
        Alat(id = "5", kodeAlat = "TRF-003", namaAlat = "Trafo C", latitude = -3.3230, longitude = 114.5940, kondisi = "Normal")
    )

    fun loadEquipment(id: String?) {
        if (id == null) {
            _state.update { it.copy(isEditMode = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isEditMode = true, equipmentId = id) }
            delay(500) // Simulate network

            val equipment = allEquipments.find { it.id == id }
            if (equipment != null) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        namaAlat = equipment.namaAlat,
                        kodeAlat = equipment.kodeAlat,
                        tipePeralatan = "Transformator", // Mock type
                        status = equipment.kondisi,
                        lokasi = "Lokasi Mock", // Mock location name
                        latitude = equipment.latitude,
                        longitude = equipment.longitude
                    ) 
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Alat tidak ditemukan") }
            }
        }
    }

    fun onNamaChange(value: String) {
        _state.update { it.copy(namaAlat = value) }
    }

    fun onKodeChange(value: String) {
        _state.update { it.copy(kodeAlat = value) }
    }

    fun onTipeChange(value: String) {
        _state.update { it.copy(tipePeralatan = value) }
    }

    fun onStatusChange(value: String) {
        _state.update { it.copy(status = value) }
    }

    fun onLokasiChange(value: String) {
        _state.update { it.copy(lokasi = value) }
    }

    fun onSaveEquipment() {
        viewModelScope.launch {
            val currentState = _state.value
            
            // Basic Validation
            if (currentState.namaAlat.isBlank() || currentState.kodeAlat.isBlank()) {
                _state.update { it.copy(error = "Nama dan Kode Alat wajib diisi") }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }
            delay(1000) // Simulate save
            
            _state.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
