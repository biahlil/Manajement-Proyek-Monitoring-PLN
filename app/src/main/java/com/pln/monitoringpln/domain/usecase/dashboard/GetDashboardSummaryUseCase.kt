package com.pln.monitoringpln.domain.usecase.dashboard

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository

class GetDashboardSummaryUseCase(private val repository: DashboardRepository) {
    operator fun invoke(technicianId: String? = null): kotlinx.coroutines.flow.Flow<DashboardSummary> = repository.getDashboardSummary(technicianId)
}
