package com.pln.monitoringpln.presentation.task.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class TaskDetailViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    // Mock Data Repositories (In real app, inject these)
    private val allTasks = listOf(
        Tugas(id = "1", deskripsi = "Inspeksi Gardu A", idAlat = "1", idTeknisi = "1", status = "In Progress", tglJatuhTempo = Date()),
        Tugas(id = "2", deskripsi = "Perbaikan Trafo B", idAlat = "2", idTeknisi = "1", status = "To Do", tglJatuhTempo = Date()),
        Tugas(id = "3", deskripsi = "Pengecekan Kabel C", idAlat = "3", idTeknisi = "3", status = "Done", tglJatuhTempo = Date()),
        Tugas(id = "4", deskripsi = "Ganti Oli Trafo D", idAlat = "4", idTeknisi = "2", status = "In Progress", tglJatuhTempo = Date()),
        Tugas(id = "5", deskripsi = "Inspeksi Rutin E", idAlat = "5", idTeknisi = "1", status = "To Do", tglJatuhTempo = Date())
    )

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

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Check Role
            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull() == "admin"

            // Simulate Network Delay
            delay(500)

            val task = allTasks.find { it.id == taskId }
            if (task != null) {
                val equipment = allEquipments.find { it.id == task.idAlat }
                val technician = allTechnicians.find { it.id == task.idTeknisi }
                
                _state.update { 
                    it.copy(
                        isLoading = false,
                        task = task,
                        equipment = equipment,
                        technician = technician,
                        isAdmin = isAdmin,
                        // Initialize Report Fields from Task
                        condition = "Kondisi awal...", // Mock existing data
                        equipmentStatus = "Normal",
                        taskStatus = task.status
                    ) 
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Tugas tidak ditemukan") }
            }
        }
    }

    fun onDeleteTask() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            // Mock Delete
            delay(1000)
            _state.update { it.copy(showDeleteDialog = false, isDeleted = true) }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }

    // Report Actions
    fun onConditionChange(condition: String) {
        _state.update { it.copy(condition = condition) }
    }

    fun onEquipmentStatusChange(status: String) {
        _state.update { it.copy(equipmentStatus = status) }
    }

    fun onTaskStatusChange(status: String) {
        _state.update { it.copy(taskStatus = status) }
    }

    fun onProofSelected(uri: String) {
        _state.update { it.copy(proofUri = uri) }
    }

    fun onSaveReport() {
        viewModelScope.launch {
            _state.update { it.copy(isSavingReport = true) }
            delay(1500) // Mock save delay
            _state.update { it.copy(isSavingReport = false, isReportSaved = true) }
        }
    }
}
