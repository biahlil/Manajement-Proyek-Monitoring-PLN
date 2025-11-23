package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.AlatHistory
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository

class GetAlatHistoryUseCase(
    private val alatRepository: AlatRepository,
    private val tugasRepository: TugasRepository,
) {

    suspend operator fun invoke(idAlat: String): Result<AlatHistory> {
        // 1. Validasi Input
        if (idAlat.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Alat tidak boleh kosong."))
        }

        // 2. Ambil Data Alat
        val alatResult = alatRepository.getAlatDetail(idAlat)
        if (alatResult.isFailure) {
            return Result.failure(alatResult.exceptionOrNull() ?: Exception("Alat tidak ditemukan"))
        }
        val alat = alatResult.getOrNull()!!

        // 3. Ambil Riwayat Tugas (Jika gagal/kosong, anggap list kosong saja, jangan error seluruhnya)
        val tugasResult = tugasRepository.getTasksByAlat(idAlat)
        val listTugas = tugasResult.getOrNull() ?: emptyList()

        // 4. Return Gabungan
        return Result.success(AlatHistory(alat, listTugas))
    }
}
