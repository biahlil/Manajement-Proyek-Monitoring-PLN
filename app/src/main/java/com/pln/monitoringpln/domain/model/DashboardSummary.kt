package com.pln.monitoringpln.domain.model

data class DashboardSummary(
    val totalAlat: Int = 0,
    val totalTeknisi: Int = 0,
    val totalTugas: Int = 0,
    val tugasToDo: Int = 0,
    val tugasInProgress: Int = 0,
    val tugasDone: Int = 0,
) {
    // Helper logic di Domain Model (opsional, untuk memudahkan UI)
    fun getCompletionRate(): Double {
        if (totalTugas == 0) return 0.0
        return (tugasDone.toDouble() / totalTugas) * 100
    }
}
