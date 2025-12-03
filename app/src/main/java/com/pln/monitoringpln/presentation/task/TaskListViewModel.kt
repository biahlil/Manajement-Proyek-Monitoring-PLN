package com.pln.monitoringpln.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.usecase.tugas.ObserveTasksUseCase
import com.pln.monitoringpln.domain.usecase.tugas.SyncTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.pln.monitoringpln.domain.repository.UserRepository

class TaskListViewModel(
    private val authRepository: AuthRepository,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val syncTasksUseCase: SyncTasksUseCase,
    private val alatRepository: AlatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskListState(isLoading = true))
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    private var allTasksCache: List<Tugas> = emptyList()
    private var currentUserId: String = ""
    private var isUserAdmin: Boolean = false

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Check User Role & ID
            val roleResult = authRepository.getUserRole()
            val role = roleResult.getOrDefault("technician")
            isUserAdmin = role.equals("admin", ignoreCase = true)
            currentUserId = authRepository.getCurrentUserId() ?: ""
            
            android.util.Log.d("TaskListViewModel", "User: $currentUserId, Role: $role, IsAdmin: $isUserAdmin")

            // Trigger Sync
            launch {
                syncTasksUseCase()
            }

            // Fetch Technicians for Name Mapping
            launch {
                val result = userRepository.getAllTeknisi()
                if (result.isSuccess) {
                    val technicians = result.getOrNull() ?: emptyList()
                    android.util.Log.d("TaskListViewModel", "Fetched ${technicians.size} technicians: ${technicians.map { "${it.id}:${it.namaLengkap}" }}")
                    val nameMap = technicians.associate { it.id to it.namaLengkap }
                    _state.update { it.copy(technicianNames = nameMap) }
                } else {
                    android.util.Log.e("TaskListViewModel", "Failed to fetch technicians", result.exceptionOrNull())
                }
            }

            // Fetch Equipments for Name Mapping
            launch {
                alatRepository.getAllAlat().collect { equipments ->
                    val equipmentMap = equipments.associate { it.id to it.namaAlat }
                    _state.update { it.copy(equipmentNames = equipmentMap) }
                }
            }

            // Observe Tasks
            val teknisiId = if (isUserAdmin) null else currentUserId
            observeTasksUseCase(teknisiId).collect { tasks ->
                android.util.Log.d("TaskListViewModel", "Observed ${tasks.size} tasks for teknisiId: $teknisiId")
                allTasksCache = tasks
                applyFilters()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery
        
        // Filter by Search
        val filtered = if (query.isBlank()) {
            allTasksCache
        } else {
            allTasksCache.filter {
                it.judul.contains(query, ignoreCase = true) ||
                it.deskripsi.contains(query, ignoreCase = true) ||
                it.idAlat.contains(query, ignoreCase = true)
            }
        }

        _state.update {
            it.copy(
                isLoading = false,
                tasks = allTasksCache,
                filteredTasks = filtered,
                isAdmin = isUserAdmin
            )
        }
    }

    fun onDeleteTask(task: Tugas) {
        _state.update { it.copy(showDeleteConfirmation = true, taskToDelete = task) }
    }

    fun onConfirmDelete() {
        val taskToDelete = _state.value.taskToDelete
        if (taskToDelete != null) {
            viewModelScope.launch {
                // TODO: Implement Delete in Repository
                // tugasRepository.deleteTask(taskToDelete.id)
                // For now just hide dialog
                _state.update { 
                    it.copy(
                        showDeleteConfirmation = false,
                        taskToDelete = null
                    )
                }
            }
        }
    }

    fun onDismissDelete() {
        _state.update { it.copy(showDeleteConfirmation = false, taskToDelete = null) }
    }
}
