package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository

class CompleteTaskUseCase(
    private val tugasRepository: TugasRepository,
    private val alatRepository: AlatRepository
) {

    suspend operator fun invoke(
        taskId: String,
        photoBytes: ByteArray,
        newCondition: String
    ): Result<Unit> {
        // 1. Validasi Input
        if (taskId.isBlank()) return Result.failure(IllegalArgumentException("ID Tugas tidak valid."))
        if (photoBytes.isEmpty()) return Result.failure(IllegalArgumentException("Foto bukti wajib diunggah."))
        if (newCondition.isBlank()) return Result.failure(IllegalArgumentException("Kondisi alat wajib diisi."))

        // 2. Ambil Detail Tugas (Untuk tahu ID Alat)
        val taskResult = tugasRepository.getTaskDetail(taskId)
        if (taskResult.isFailure) return Result.failure(taskResult.exceptionOrNull() ?: Exception("Tugas tidak ditemukan"))
        val task = taskResult.getOrNull()!!

        // 3. Upload Bukti
        val uploadResult = tugasRepository.uploadTaskProof(taskId, photoBytes)
        if (uploadResult.isFailure) return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Gagal upload bukti"))

        // 4. Update Status Tugas -> "Done"
        val updateTaskResult = tugasRepository.updateTaskStatus(taskId, "Done")
        if (updateTaskResult.isFailure) return Result.failure(updateTaskResult.exceptionOrNull() ?: Exception("Gagal update status tugas"))

        // 5. Update Kondisi Alat
        // (Menggunakan idAlat dari tugas yang diambil di langkah 2)
        return alatRepository.updateAlatCondition(task.idAlat, newCondition)
    }
}