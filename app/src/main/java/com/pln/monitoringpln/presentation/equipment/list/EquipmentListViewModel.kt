package com.pln.monitoringpln.presentation.equipment.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EquipmentListViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val alatRepository: AlatRepository,
    private val tugasRepository: com.pln.monitoringpln.domain.repository.TugasRepository,
) : ViewModel() {

    private val savedSearchQuery: String = savedStateHandle.get<String>("searchQuery") ?: ""
    private val _state = MutableStateFlow(EquipmentListState(isLoading = true, searchQuery = savedSearchQuery))
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
        savedStateHandle["filterType"] = filterType
        // Always apply filter to handle back navigation or re-attachment
        applyFilters()
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        savedStateHandle["searchQuery"] = query // Persist search
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val query = _state.value.searchQuery
            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull()?.equals("admin", ignoreCase = true) == true

            // 1. Filter by Category
            // 1. Filter by Category
            val categoryFiltered = when (currentFilterType) {
                "normal_equipment" -> allEquipmentsCache.filter { it.status == "Normal" }
                "warning_equipment" -> allEquipmentsCache.filter { it.status == "Perlu Perhatian" }
                "broken_equipment" -> allEquipmentsCache.filter { it.status == "Rusak" }
                "my_equipment" -> {
                    // Filter: Equipment that has tasks assigned to this technician
                    val currentUserId = authRepository.getCurrentUserId()
                    if (currentUserId != null) {
                        val technicianTasks = tugasRepository.getTasksByTeknisi(currentUserId).getOrDefault(emptyList())
                        val technicianEquipmentIds = technicianTasks.map { it.idAlat }.distinct()
                        allEquipmentsCache.filter { it.id in technicianEquipmentIds }
                    } else {
                        emptyList()
                    }
                }

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
                    isAdmin = isAdmin,
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
                            equipmentToDelete = null,
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
