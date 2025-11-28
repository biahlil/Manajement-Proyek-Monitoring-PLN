package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.domain.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DashboardRepositoryImpl(
    private val alatDao: com.pln.monitoringpln.data.local.dao.AlatDao,
    private val tugasDao: com.pln.monitoringpln.data.local.dao.TugasDao
) : DashboardRepository {

    override suspend fun getDashboardSummary(): Result<DashboardSummary> = coroutineScope {
        try {
            val totalAlatDeferred = async { alatDao.countAll() }
            val totalTugasDeferred = async { tugasDao.countAll() }
            val tugasToDoDeferred = async { tugasDao.countByStatus("To Do") }
            val tugasInProgressDeferred = async { tugasDao.countByStatus("In Progress") }
            val tugasDoneDeferred = async { tugasDao.countByStatus("Done") }

            // Note: Total Teknisi is not available offline yet (requires UserDao).
            // Returning 0 or cached value would be ideal.
            val totalTeknisi = 0

            val summary = DashboardSummary(
                totalAlat = totalAlatDeferred.await(),
                totalTeknisi = totalTeknisi,
                totalTugas = totalTugasDeferred.await(),
                tugasToDo = tugasToDoDeferred.await(),
                tugasInProgress = tugasInProgressDeferred.await(),
                tugasDone = tugasDoneDeferred.await()
            )

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
