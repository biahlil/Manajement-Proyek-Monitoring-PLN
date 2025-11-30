package com.pln.monitoringpln.presentation.dashboard

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User

data class DashboardState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val summary: DashboardSummary = DashboardSummary(),
    val technicians: List<User> = emptyList(),
    val inProgressTasks: List<Tugas> = emptyList(),
    val technicianTasks: List<Tugas> = emptyList(), // For "Tugas Hari Ini"
    val activeWarnings: List<Tugas> = emptyList(), // For "Peringatan Aktif"
    val isAdmin: Boolean = false // Should be determined by auth
)
