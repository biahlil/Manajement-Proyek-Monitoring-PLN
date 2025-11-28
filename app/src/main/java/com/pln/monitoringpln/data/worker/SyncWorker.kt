package com.pln.monitoringpln.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val alatRepository: AlatRepository by inject()
    private val tugasRepository: TugasRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sync Alat
            val alatResult = alatRepository.sync()
            
            // Sync Tugas
            val tugasResult = tugasRepository.sync()

            if (alatResult.isSuccess && tugasResult.isSuccess) {
                Result.success()
            } else {
                // If sync fails, retry later
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
