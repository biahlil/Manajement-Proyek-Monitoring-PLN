package com.pln.monitoringpln.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.DashboardRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val authRepository: AuthRepository,
    // private val userRepository: UserRepository, // Uncomment when ready
    // private val tugasRepository: TugasRepository // Uncomment when ready
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Check User Role
            val roleResult = authRepository.getUserRole()
            val isAdmin = roleResult.getOrNull() == "admin" // Adjust based on actual role string

            // Fetch Summary
            val summaryResult = dashboardRepository.getDashboardSummary()
            val currentSummary = summaryResult.getOrDefault(com.pln.monitoringpln.domain.model.DashboardSummary())
            
            // Mocking breakdown for UI demo
            val updatedSummary = currentSummary.copy(
                totalAlatNormal = (currentSummary.totalAlat * 0.7).toInt(),
                totalAlatPerluPerhatian = (currentSummary.totalAlat * 0.2).toInt(),
                totalAlatRusak = (currentSummary.totalAlat * 0.1).toInt()
            )

            // Mocking other data for now since Repositories might not have specific methods yet
            // In a real app, we would call userRepository.getTechnicians() and tugasRepository.getPendingApprovals()

            _state.update { 
                it.copy(
                    isLoading = false,
                    isAdmin = isAdmin,
                    summary = updatedSummary,
                    // Mock data for Technician
                    technicianTasks = if (!isAdmin) List(2) { 
                        com.pln.monitoringpln.domain.model.Tugas(
                            id = "TASK-00${it+1}",
                            deskripsi = "Trafo KTG-00${it+1}", 
                            idAlat = "1", 
                            idTeknisi = "1", 
                            tglJatuhTempo = java.util.Date(),
                            status = "In Progress"
                        ) 
                    } else emptyList(),
                    activeWarnings = if (!isAdmin) List(2) { 
                        com.pln.monitoringpln.domain.model.Tugas(
                            id = "WARN-00${it+1}",
                            deskripsi = "Warning Trafo ${it+1}", 
                            idAlat = "1", 
                            idTeknisi = "1", 
                            tglJatuhTempo = java.util.Date(),
                            status = "To Do"
                        ) 
                    } else emptyList()
                )
            }
        }
    }
}
