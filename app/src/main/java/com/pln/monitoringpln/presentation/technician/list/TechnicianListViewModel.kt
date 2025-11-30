package com.pln.monitoringpln.presentation.technician.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TechnicianListViewModel : ViewModel() {

    private val _state = MutableStateFlow(TechnicianListState())
    val state: StateFlow<TechnicianListState> = _state.asStateFlow()

    // Mock Data
    private val allTechnicians = listOf(
        User(id = "1", name = "Rusman Hadi", role = "teknisi", email = "rusman@pln.co.id"),
        User(id = "2", name = "Ahmad Zaky", role = "teknisi", email = "zaky@pln.co.id"),
        User(id = "3", name = "Budi Santoso", role = "teknisi", email = "budi@pln.co.id"),
        User(id = "4", name = "Siti Aminah", role = "teknisi", email = "siti@pln.co.id")
    )

    init {
        loadTechnicians()
    }

    fun loadTechnicians() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500) // Simulate network

            _state.update { 
                it.copy(
                    isLoading = false,
                    technicians = allTechnicians,
                    filteredTechnicians = allTechnicians
                ) 
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { state ->
            val filtered = if (query.isBlank()) {
                state.technicians
            } else {
                state.technicians.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredTechnicians = filtered)
        }
    }

    fun onDeleteTechnician(technician: User) {
        _state.update { it.copy(showDeleteDialog = true, technicianToDelete = technician) }
    }

    fun onConfirmDelete() {
        val technician = _state.value.technicianToDelete ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            delay(1000) // Simulate delete API call
            
            // Remove from list
            val updatedList = _state.value.technicians.filter { it.id != technician.id }
            val updatedFiltered = _state.value.filteredTechnicians.filter { it.id != technician.id }
            
            _state.update { 
                it.copy(
                    isDeleting = false, 
                    showDeleteDialog = false, 
                    technicianToDelete = null,
                    technicians = updatedList,
                    filteredTechnicians = updatedFiltered
                ) 
            }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false, technicianToDelete = null) }
    }
}
