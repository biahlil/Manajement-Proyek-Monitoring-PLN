package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.TugasRepository

class UpdateTaskStatusUseCase(private val repository: TugasRepository) {

    // Daftar status yang diizinkan sistem (Database Constaint: UPPERCASE)
    private val validStatuses = listOf("TODO", "IN_PROGRESS", "DONE")

    suspend operator fun invoke(taskId: String, newStatus: String): Result<Unit> {
        if (taskId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Tugas tidak valid."))
        }

        if (newStatus !in validStatuses) {
            return Result.failure(IllegalArgumentException("Status tidak valid. Gunakan: TODO, IN_PROGRESS, atau DONE."))
        }

        return repository.updateTaskStatus(taskId, newStatus)
    }
}
