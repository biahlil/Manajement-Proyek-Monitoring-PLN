package com.pln.monitoringpln.domain.usecase.tugas

import android.util.Log
import com.pln.monitoringpln.domain.repository.TugasRepository

class SyncTasksUseCase(private val repository: TugasRepository) {
    suspend operator fun invoke(): Result<Unit> {
        android.util.Log.d("SyncTasksUseCase", "Starting sync...")
        val result = repository.sync()
        result.onSuccess {
            android.util.Log.d("SyncTasksUseCase", "Sync successful")
        }.onFailure {
            android.util.Log.e("SyncTasksUseCase", "Sync failed", it)
        }
        return result
    }
}
