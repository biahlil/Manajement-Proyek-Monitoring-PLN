package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.DashboardSummary

interface DashboardRepository {
    suspend fun getDashboardSummary(): Result<DashboardSummary>
}