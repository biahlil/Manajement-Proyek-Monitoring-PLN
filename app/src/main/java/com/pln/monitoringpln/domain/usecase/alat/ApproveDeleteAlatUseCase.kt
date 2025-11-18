package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.repository.AlatRepository

class ApproveDeleteAlatUseCase(private val repository: AlatRepository) {

    suspend operator fun invoke(alatId: String, technicianId: String): Result<Unit> {
        // 1. Validasi Input
        if (alatId.isBlank()) return Result.failure(Exception("ID Alat tidak boleh kosong"))

        // 2. Ambil detail alat
        val alatResult = repository.getAlatDetail(alatId)
        if (alatResult.isFailure) {
            return Result.failure(alatResult.exceptionOrNull() ?: Exception("Alat tidak ditemukan"))
        }

        val alat = alatResult.getOrNull()!!

        // 3. CEK STATUS (ATURAN BISNIS BARU DARI TES)
        // Teknisi tidak boleh menghapus jika Admin belum request (Status belum PENDING_DELETE)
        if (alat.status != "PENDING_DELETE") {
            return Result.failure(Exception("Penghapusan belum diajukan oleh Admin."))
        }

        // 4. Cek Otorisasi (Teknisi Terakhir)
        if (alat.lastModifiedById != technicianId) {
            return Result.failure(Exception("Anda bukan teknisi terakhir yang mengubah alat ini."))
        }

        // 5. Hapus Permanen
        return repository.deleteAlat(alatId)
    }
}
