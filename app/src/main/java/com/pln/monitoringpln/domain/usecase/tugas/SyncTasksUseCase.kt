package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.TugasRepository

class SyncTasksUseCase(private val repository: TugasRepository) {
    suspend operator fun invoke(): Result<Unit> {
        // Log.d("SyncTasksUseCase", "Starting sync...")
        val result = repository.sync()
        result.onSuccess {
            // Log.d("SyncTasksUseCase", "Sync successful")
        }.onFailure {
            // Log.e("SyncTasksUseCase", "Sync failed", it)
        }
        return result
    }
}
