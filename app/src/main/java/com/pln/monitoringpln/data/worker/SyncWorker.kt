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
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val alatRepository: AlatRepository by inject()
    private val tugasRepository: TugasRepository by inject()
    private val userRepository: com.pln.monitoringpln.domain.repository.UserRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sync Alat
            val alatResult = alatRepository.sync()

            // Sync Tugas
            val tugasResult = tugasRepository.sync()

            // Sync Profile
            val profileResult = userRepository.syncProfile()

            if (alatResult.isSuccess && tugasResult.isSuccess) {
                scheduleNextWork()
                Result.success()
            } else {
                // If sync fails, we still schedule next work to keep the loop alive,
                // or we could return retry() which uses exponential backoff.
                // Given the requirement for 5 min cycle, let's schedule next and return success/failure.
                // If we return retry(), WorkManager controls the timing.
                // Let's schedule next work and return success to enforce our 5 min timer.
                scheduleNextWork()
                Result.success()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            scheduleNextWork()
            Result.failure()
        }
    }

    private fun scheduleNextWork() {
        val workManager = androidx.work.WorkManager.getInstance(applicationContext)
        val nextRequest = androidx.work.OneTimeWorkRequestBuilder<SyncWorker>()
            .setInitialDelay(2, java.util.concurrent.TimeUnit.MINUTES)
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build(),
            )
            .addTag("SyncWorker")
            .build()

        workManager.enqueueUniqueWork(
            "SyncWorker",
            androidx.work.ExistingWorkPolicy.REPLACE, // Replace existing to reset timer
            nextRequest,
        )
    }
}
