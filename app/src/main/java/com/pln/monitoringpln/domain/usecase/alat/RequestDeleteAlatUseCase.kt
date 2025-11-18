package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.repository.AlatRepository

class RequestDeleteAlatUseCase(private val repository: AlatRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) return Result.failure(Exception("ID tidak valid"))
        // Admin hanya mengubah status, tidak menghapus data fisik
        return repository.requestDeleteAlat(id)
    }
}