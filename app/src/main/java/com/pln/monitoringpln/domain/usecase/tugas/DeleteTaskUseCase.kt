package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.TugasRepository

class DeleteTaskUseCase(
    private val tugasRepository: TugasRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        if (taskId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Tugas tidak valid"))
        }
        return tugasRepository.deleteTask(taskId)
    }
}
