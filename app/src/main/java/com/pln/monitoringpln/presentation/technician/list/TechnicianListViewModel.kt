package com.pln.monitoringpln.presentation.technician.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TechnicianListViewModel(
    private val getTechniciansUseCase: com.pln.monitoringpln.domain.usecase.technician.GetTechniciansUseCase,
    private val observeTasksUseCase: com.pln.monitoringpln.domain.usecase.tugas.ObserveTasksUseCase,
    private val userRepository: com.pln.monitoringpln.domain.repository.UserRepository,
    private val getAllAlatUseCase: com.pln.monitoringpln.domain.usecase.alat.GetAllAlatUseCase,
    private val deleteUserUseCase: com.pln.monitoringpln.domain.usecase.user.DeleteUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TechnicianListState())
    val state: StateFlow<TechnicianListState> = _state.asStateFlow()

    init {
        loadTechnicians()
    }

    fun loadTechnicians() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. Fetch Technicians
            launch {
                getTechniciansUseCase().collect { technicians ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            technicians = technicians,
                            filteredTechnicians = technicians,
                        )
                    }
                }
            }

            // 2. Fetch Equipment & Tasks to map equipment to technician
            launch {
                kotlinx.coroutines.flow.combine(
                    getAllAlatUseCase(),
                    observeTasksUseCase(),
                ) { alatList, tasks ->
                    val equipmentMap = alatList.associate { it.id to it.namaAlat }

                    // Task Counts
                    val counts = tasks
                        .filter { it.status != "Done" }
                        .groupBy { it.idTeknisi }
                        .mapValues { it.value.size }

                    // Random Equipment per Technician
                    val techEquipment = tasks
                        .groupBy { it.idTeknisi }
                        .mapValues { (_, techTasks) ->
                            if (techTasks.isNotEmpty()) {
                                val randomTask = techTasks.random()
                                equipmentMap[randomTask.idAlat] ?: "Alat Tidak Dikenal (${randomTask.idAlat})"
                            } else {
                                "Belum ada alat"
                            }
                        }

                    Triple(counts, techEquipment, equipmentMap)
                }.collect { (counts, techEquipment, _) ->
                    _state.update {
                        it.copy(
                            technicianTaskCounts = counts,
                            technicianEquipment = techEquipment,
                        )
                    }
                }
            }
        }
    }

    fun refreshTechnicians() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.refreshTeknisi()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { state ->
            val filtered = if (query.isBlank()) {
                state.technicians
            } else {
                state.technicians.filter {
                    it.namaLengkap.contains(query, ignoreCase = true) ||
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

            val result = deleteUserUseCase(technician.id)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        showDeleteDialog = false,
                        technicianToDelete = null,
                    )
                }
                // Refresh list to ensure UI is in sync
                loadTechnicians()
            } else {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        // Optionally show error
                    )
                }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false, technicianToDelete = null) }
    }
}
