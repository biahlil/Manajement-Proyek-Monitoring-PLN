package com.pln.monitoringpln.presentation.task.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.User
import java.time.LocalDate

class AddEditTaskViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddEditTaskState())
    val state: StateFlow<AddEditTaskState> = _state.asStateFlow()

    private val allEquipments = listOf(
        Alat(id = "1", kodeAlat = "TRF-001", namaAlat = "Trafo A", latitude = -3.3194, longitude = 114.5908, kondisi = "Normal"),
        Alat(id = "2", kodeAlat = "TRF-002", namaAlat = "Trafo B", latitude = -3.3200, longitude = 114.5910, kondisi = "Normal"),
        Alat(id = "3", kodeAlat = "CBL-001", namaAlat = "Kabel Bawah Tanah", latitude = -3.3210, longitude = 114.5920, kondisi = "Rusak"),
        Alat(id = "4", kodeAlat = "PNL-001", namaAlat = "Panel Distribusi", latitude = -3.3220, longitude = 114.5930, kondisi = "Perlu Perhatian"),
        Alat(id = "5", kodeAlat = "TRF-003", namaAlat = "Trafo C", latitude = -3.3230, longitude = 114.5940, kondisi = "Normal")
    )

    private val allTechnicians = listOf(
        User(id = "1", email = "rusman@pln.co.id", namaLengkap = "Rusman Hadi", role = "Teknisi"),
        User(id = "2", email = "budi@pln.co.id", namaLengkap = "Budi Santoso", role = "Teknisi"),
        User(id = "3", email = "ahmad@pln.co.id", namaLengkap = "Ahmad Yani", role = "Teknisi"),
        User(id = "4", email = "siti@pln.co.id", namaLengkap = "Siti Aminah", role = "Teknisi")
    )

    init {
        // Load initial data
        _state.update { 
            it.copy(
                availableEquipments = allEquipments,
                availableTechnicians = allTechnicians
            ) 
        }
    }

    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun onEquipmentSearchQueryChange(query: String) {
        _state.update { 
            it.copy(
                equipmentSearchQuery = query,
                availableEquipments = if (query.isBlank()) {
                    allEquipments
                } else {
                    allEquipments.filter { eq -> 
                        eq.namaAlat.contains(query, ignoreCase = true) || 
                        eq.kodeAlat.contains(query, ignoreCase = true) 
                    }
                }
            ) 
        }
    }

    fun onEquipmentSelected(equipment: Alat) {
        _state.update { 
            it.copy(
                selectedEquipment = equipment,
                equipmentSearchQuery = "${equipment.namaAlat} - ${equipment.kodeAlat}",
            ) 
        }
    }

    fun onDeadlineSelected(date: LocalDate) {
        _state.update { it.copy(deadline = date) }
    }

    fun onTechnicianSelected(technician: User) {
        _state.update { it.copy(selectedTechnician = technician) }
    }

    fun onSaveTask() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            // Validation
            val currentState = _state.value
            if (currentState.title.isBlank() || 
                currentState.selectedEquipment == null || 
                currentState.deadline == null || 
                currentState.selectedTechnician == null
            ) {
                _state.update { it.copy(isSaving = false, error = "Mohon lengkapi semua field bertanda *") }
                return@launch
            }

            // Mock Save Delay
            delay(1500)

            _state.update { it.copy(isSaving = false, isTaskSaved = true) }
        }
    }
}
