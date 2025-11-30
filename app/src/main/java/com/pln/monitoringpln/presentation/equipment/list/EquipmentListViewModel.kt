package com.pln.monitoringpln.presentation.equipment.list

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

class EquipmentListViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EquipmentListState())
    val state: StateFlow<EquipmentListState> = _state.asStateFlow()

    // Mock Data
    private val allEquipments = listOf(
        Alat(id = "1", kodeAlat = "TRF-001", namaAlat = "Trafo Kayu Tangi 1", latitude = -3.3194, longitude = 114.5908, kondisi = "Normal"),
        Alat(id = "2", kodeAlat = "TRF-002", namaAlat = "Trafo Kayu Tangi 2", latitude = -3.3200, longitude = 114.5910, kondisi = "Normal"),
        Alat(id = "3", kodeAlat = "CBL-001", namaAlat = "Kabel Bawah Tanah", latitude = -3.3210, longitude = 114.5920, kondisi = "Rusak"),
        Alat(id = "4", kodeAlat = "PNL-001", namaAlat = "Panel Distribusi", latitude = -3.3220, longitude = 114.5930, kondisi = "Perlu Perhatian"),
        Alat(id = "5", kodeAlat = "TRF-003", namaAlat = "Trafo C", latitude = -3.3230, longitude = 114.5940, kondisi = "Normal")
    )

    // Mock Technician Assignment (Equipment IDs assigned to Technician ID "1")
    private val technicianAssignment = listOf("1", "2", "5")

    init {
        loadEquipment()
    }

    fun loadEquipment() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull() == "admin"
            
            delay(500) // Mock delay

            val equipment = if (isAdmin) {
                allEquipments
            } else {
                // Filter for Technician (Mock ID "1")
                allEquipments.filter { it.id in technicianAssignment }
            }

            _state.update { 
                it.copy(
                    isLoading = false,
                    equipmentList = equipment,
                    filteredEquipmentList = equipment,
                    isAdmin = isAdmin
                ) 
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { state ->
            val filtered = if (query.isBlank()) {
                state.equipmentList
            } else {
                state.equipmentList.filter {
                    it.namaAlat.contains(query, ignoreCase = true) ||
                    it.kodeAlat.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredEquipmentList = filtered)
        }
    }
    fun onDeleteEquipment(equipment: Alat) {
        _state.update { it.copy(showDeleteDialog = true, equipmentToDelete = equipment) }
    }

    fun onConfirmDelete() {
        val equipment = _state.value.equipmentToDelete ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            delay(1000) // Mock delete delay
            
            // Remove from list
            val updatedList = _state.value.equipmentList.filter { it.id != equipment.id }
            val updatedFiltered = _state.value.filteredEquipmentList.filter { it.id != equipment.id }
            
            _state.update { 
                it.copy(
                    isDeleting = false,
                    showDeleteDialog = false,
                    equipmentToDelete = null,
                    equipmentList = updatedList,
                    filteredEquipmentList = updatedFiltered
                ) 
            }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false, equipmentToDelete = null) }
    }
}
