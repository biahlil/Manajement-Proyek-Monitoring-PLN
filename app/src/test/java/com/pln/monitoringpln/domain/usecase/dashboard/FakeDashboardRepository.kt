package com.pln.monitoringpln.domain.usecase.dashboard

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository

class FakeDashboardRepository : DashboardRepository {

    var summaryToReturn: DashboardSummary? = null
    var shouldFail = false

    override suspend fun getDashboardSummary(): Result<DashboardSummary> {
        println("  ➡️ [FakeRepo] Request Dashboard Summary...")

        if (shouldFail) {
            println("     ❌ Gagal: Simulasi Error Database")
            return Result.failure(Exception("Gagal mengambil data dashboard"))
        }

        val data = summaryToReturn ?: DashboardSummary()
        println("     ✅ Sukses: Total Tugas ${data.totalTugas}, Done ${data.tugasDone}")
        return Result.success(data)
    }
}