package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.TugasRepository

class UpdateTaskStatusUseCase(private val repository: TugasRepository) {

    // Daftar status yang diizinkan sistem
    private val validStatuses = listOf("To Do", "In Progress", "Done")

    suspend operator fun invoke(taskId: String, newStatus: String): Result<Unit> {
        if (taskId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Tugas tidak valid."))
        }

        if (newStatus !in validStatuses) {
            return Result.failure(IllegalArgumentException("Status tidak valid. Gunakan: To Do, In Progress, atau Done."))
        }

        return repository.updateTaskStatus(taskId, newStatus)
    }
}