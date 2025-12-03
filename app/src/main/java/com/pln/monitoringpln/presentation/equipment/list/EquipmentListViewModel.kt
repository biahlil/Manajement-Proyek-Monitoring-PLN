package com.pln.monitoringpln.presentation.equipment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.AlatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.SavedStateHandle

class EquipmentListViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val alatRepository: AlatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EquipmentListState(isLoading = true))
    val state: StateFlow<EquipmentListState> = _state.asStateFlow()

    private var allEquipmentsCache: List<Alat> = emptyList()
    private var currentFilterType: String = savedStateHandle.get<String>("filterType") ?: "all_equipment"
    
    init {
        viewModelScope.launch {
            alatRepository.getAllAlat().collect { list ->
                allEquipmentsCache = list
                applyFilters()
            }
        }
    }

    fun setFilter(filterType: String) {
        if (currentFilterType != filterType) {
            currentFilterType = filterType
            applyFilters()
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val query = _state.value.searchQuery
            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull()?.equals("admin", ignoreCase = true) == true

            // 1. Filter by Category
            val categoryFiltered = when (currentFilterType) {
                "normal_equipment" -> allEquipmentsCache.filter { it.kondisi == "Normal" }
                "warning_equipment" -> allEquipmentsCache.filter { it.kondisi == "Perlu Perhatian" }
                "broken_equipment" -> allEquipmentsCache.filter { it.kondisi == "Rusak" }
                else -> allEquipmentsCache
            }

            // 2. Filter by Search
            val finalFiltered = if (query.isBlank()) {
                categoryFiltered
            } else {
                categoryFiltered.filter {
                    it.namaAlat.contains(query, ignoreCase = true) ||
                    it.kodeAlat.contains(query, ignoreCase = true)
                }
            }

            _state.update { 
                it.copy(
                    isLoading = false,
                    equipmentList = categoryFiltered,
                    filteredEquipmentList = finalFiltered,
                    isAdmin = isAdmin
                ) 
            }
        }
    }
    
    fun onDeleteEquipment(equipment: Alat) {
        _state.update { it.copy(showDeleteDialog = true, equipmentToDelete = equipment) }
    }

    fun onConfirmDelete() {
        val equipment = _state.value.equipmentToDelete ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            
            alatRepository.archiveAlat(equipment.id)
                .onSuccess {
                    _state.update { 
                        it.copy(
                            isDeleting = false,
                            showDeleteDialog = false,
                            equipmentToDelete = null
                        ) 
                    }
                }
                .onFailure {
                    _state.update { it.copy(isDeleting = false) } // Handle error
                }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false, equipmentToDelete = null) }
    }
}
