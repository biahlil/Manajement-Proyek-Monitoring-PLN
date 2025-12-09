package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository

class CompleteTaskUseCase(
    private val tugasRepository: TugasRepository,
    private val alatRepository: AlatRepository,
) {

    suspend operator fun invoke(
        taskId: String,
        photoBytes: ByteArray?,
        newCondition: String, // Description (Text)
        equipmentStatus: String, // Health Status (Normal/Rusak)
        currentProofUrl: String? = null,
    ): Result<Unit> {
        // 1. Validasi Input
        if (taskId.isBlank()) return Result.failure(IllegalArgumentException("ID Tugas tidak valid."))
        if ((photoBytes == null || photoBytes.isEmpty()) && currentProofUrl.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Foto bukti wajib diunggah."))
        }
        if (newCondition.isBlank()) return Result.failure(IllegalArgumentException("Kondisi alat wajib diisi."))
        if (equipmentStatus.isBlank()) return Result.failure(IllegalArgumentException("Status alat wajib dipilih."))

        // 2. Upload Bukti (Jika ada photoBytes baru)
        val proofUrl = if (photoBytes != null) {
            val uploadResult = tugasRepository.uploadTaskProof(taskId, photoBytes)
            if (uploadResult.isFailure) return Result.failure(
                uploadResult.exceptionOrNull() ?: Exception("Gagal upload bukti")
            )
            uploadResult.getOrNull()!!
        } else {
            currentProofUrl!!
        }

        // 3. Update Status Tugas -> "Done" & Simpan Bukti & Deskripsi Kondisi (newCondition)
        val updateTaskResult = tugasRepository.completeTugas(taskId, proofUrl, newCondition)
        if (updateTaskResult.isFailure) return Result.failure(
            updateTaskResult.exceptionOrNull() ?: Exception("Gagal update status tugas")
        )

        // 4. Update Kondisi & Status Alat
        // Ambil detail tugas untuk dapat idAlat
        val taskResult = tugasRepository.getTaskDetail(taskId)
        if (taskResult.isFailure) return Result.failure(
            taskResult.exceptionOrNull() ?: Exception("Tugas tidak ditemukan")
        )
        val task = taskResult.getOrNull()!!

        val alatResult = alatRepository.updateAlatStatusAndCondition(task.idAlat, equipmentStatus, newCondition)
        if (alatResult.isFailure) return Result.failure(
            alatResult.exceptionOrNull() ?: Exception("Gagal update status alat")
        )

        return Result.success(Unit)
    }
}
