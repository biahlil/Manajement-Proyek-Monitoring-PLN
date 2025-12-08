package com.pln.monitoringpln.domain.usecase.dashboard

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository

class FakeDashboardRepository : DashboardRepository {

    var summaryToReturn: DashboardSummary? = null
    var shouldFail = false

    override fun getDashboardSummary(technicianId: String?): kotlinx.coroutines.flow.Flow<DashboardSummary> {
        println("  ➡️ [FakeRepo] Request Dashboard Summary...")

        if (shouldFail) {
            println("     ❌ Gagal: Simulasi Error Database")
            // Flow doesn't handle exceptions directly like Result, usually we emit a default or throw
            // For testing, let's just return empty flow or throw if needed, but Flow<T> implies success stream usually
            // or we use catch operator.
            // Let's return a flow that emits the summary.
            return kotlinx.coroutines.flow.flow { throw Exception("Gagal mengambil data dashboard") }
        }

        val data = summaryToReturn ?: DashboardSummary()
        println("     ✅ Sukses: Total Tugas ${data.totalTugas}, Done ${data.tugasDone}")
        return kotlinx.coroutines.flow.flowOf(data)
    }
}
