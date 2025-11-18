package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.repository.AlatRepository
import java.lang.IllegalArgumentException

class UpdateAlatInfoUseCase(private val repository: AlatRepository) {

    // Perhatikan: TIDAK ADA parameter 'kondisi' di sini.
    // Admin mustahil mengubah kondisi lewat fungsi ini.
    suspend operator fun invoke(
        id: String,
        namaAlat: String,
        kodeAlat: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {

        if (id.isBlank()) return Result.failure(IllegalArgumentException("ID tidak valid"))
        if (namaAlat.isBlank()) return Result.failure(IllegalArgumentException("Nama alat tidak boleh kosong."))
        if (kodeAlat.isBlank()) return Result.failure(IllegalArgumentException("Kode alat tidak boleh kosong."))

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Koordinat tidak valid."))
        }

        return repository.updateAlatInfo(id, namaAlat, kodeAlat, latitude, longitude)
    }
}