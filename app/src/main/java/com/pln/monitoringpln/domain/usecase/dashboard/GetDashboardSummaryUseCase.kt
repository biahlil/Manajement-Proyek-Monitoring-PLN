package com.pln.monitoringpln.domain.usecase.dashboard

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository

class GetDashboardSummaryUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): Result<DashboardSummary> {
        return repository.getDashboardSummary()
    }
}