package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository

class ArchiveAlatUseCase(
    private val alatRepository: AlatRepository,
    private val tugasRepository: TugasRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        // 1. Cek apakah Alat ada
        val alatResult = alatRepository.getAlatDetail(id)
        if (alatResult.isFailure) {
            return Result.failure(Exception("Alat tidak ditemukan"))
        }

        // 2. Cek apakah ada Tugas yang sedang berjalan (In Progress)
        val tasksResult = tugasRepository.getTasksByAlat(id)
        if (tasksResult.isSuccess) {
            val activeTasks = tasksResult.getOrNull()?.filter {
                it.status.equals("IN_PROGRESS", ignoreCase = true)
            }

            if (!activeTasks.isNullOrEmpty()) {
                return Result.failure(Exception("Cannot archive alat with active tasks"))
            }
        }

        // 3. Lakukan Arsip (Soft Delete)
        return alatRepository.archiveAlat(id)
    }
}
