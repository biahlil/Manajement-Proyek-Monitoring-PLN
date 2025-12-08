package com.pln.monitoringpln.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import com.pln.monitoringpln.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.pln.monitoringpln.domain.usecase.tugas.ObserveTasksUseCase
import com.pln.monitoringpln.domain.usecase.tugas.SyncTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase,
    private val authRepository: AuthRepository,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val userRepository: UserRepository,
    private val syncTasksUseCase: SyncTasksUseCase,
    private val getDashboardTechniciansUseCase: com.pln.monitoringpln.domain.usecase.dashboard.GetDashboardTechniciansUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState(isLoading = true))
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. Get User Role & ID
            val roleResult = authRepository.getUserRole()
            val role = roleResult.getOrDefault("technician")
            val isUserAdmin = role.equals("admin", ignoreCase = true)
            val currentUserId = authRepository.getCurrentUserId() ?: ""

            _state.update { it.copy(isAdmin = isUserAdmin) }

            // Trigger Sync
            launch {
                syncTasksUseCase()
            }

            // Fetch Summary (Reactive)
            launch {
                val summaryTechnicianId = if (isUserAdmin) null else currentUserId
                getDashboardSummaryUseCase(summaryTechnicianId).collect { summary ->
                    _state.update { it.copy(summary = summary, isLoading = false) }
                }
            }

            // Fetch Tasks (Reactive)
            launch {
                val teknisiId = if (isUserAdmin) null else currentUserId
                observeTasksUseCase(teknisiId).collect { tasks ->
                    _state.update {
                        it.copy(
                            // Filter "In Progress" case-insensitive, handling both "In Progress" and "IN_PROGRESS"
                            inProgressTasks = tasks.filter { t ->
                                val status = t.status.replace("_", " ")
                                status.equals("In Progress", ignoreCase = true)
                            },
                            technicianTasks = tasks.filter { t ->
                                val status = t.status.replace("_", " ")
                                !status.equals("Done", ignoreCase = true)
                            },
                        )
                    }
                }
            }

            // Fetch Technicians (Reactive + Sync)
            if (isUserAdmin) {
                // Observe Local Data
                launch {
                    getDashboardTechniciansUseCase().collect { technicians ->
                        _state.update { it.copy(technicians = technicians) }
                    }
                }

                // Trigger Refresh
                launch {
                    _state.update { it.copy(isTechniciansLoading = true) }
                    getDashboardTechniciansUseCase.refresh()
                    _state.update { it.copy(isTechniciansLoading = false) }
                }
            }
        }
    }
}
