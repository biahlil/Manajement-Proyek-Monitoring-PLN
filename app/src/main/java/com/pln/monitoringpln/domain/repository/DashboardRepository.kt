package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.DashboardSummary

interface DashboardRepository {
    fun getDashboardSummary(technicianId: String? = null): kotlinx.coroutines.flow.Flow<DashboardSummary>
}
