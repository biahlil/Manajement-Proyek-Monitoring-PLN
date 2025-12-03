package com.pln.monitoringpln.presentation.equipment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.AlatHistory
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EquipmentDetailViewModel(
    private val authRepository: AuthRepository,
    private val alatRepository: AlatRepository,
    private val tugasRepository: TugasRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EquipmentDetailState())
    val state: StateFlow<EquipmentDetailState> = _state.asStateFlow()

    fun loadEquipment(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Check Role
            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull()?.equals("admin", ignoreCase = true) == true

            // Observe Data
            combine(
                alatRepository.observeAlat(id),
                tugasRepository.observeTasksByAlat(id)
            ) { alat, tasks ->
                if (alat != null) {
                    // Sort tasks by created date descending (newest first)
                    val sortedTasks = tasks.sortedByDescending { it.tglDibuat }
                    AlatHistory(alat, sortedTasks)
                } else {
                    null
                }
            }.collect { history ->
                if (history != null) {
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            alatHistory = history, 
                            isAdmin = isAdmin,
                            error = null
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
    }

    fun onDeleteClick() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            val equipment = state.value.alatHistory?.alat ?: return@launch
            _state.update { it.copy(isDeleting = true) }
            
            val result = alatRepository.archiveAlat(equipment.id)
            
            if (result.isSuccess) {
                _state.update { 
                    it.copy(
                        isDeleting = false, 
                        showDeleteDialog = false, 
                        isDeleted = true 
                    ) 
                }
            } else {
                _state.update { 
                    it.copy(
                        isDeleting = false,
                        showDeleteDialog = false,
                        error = "Gagal menghapus alat: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }
}
