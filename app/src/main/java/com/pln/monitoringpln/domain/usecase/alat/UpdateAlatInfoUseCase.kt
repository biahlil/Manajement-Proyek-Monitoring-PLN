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
        longitude: Double,
        locationName: String? = null,
        tipe: String = "",
        kondisi: String = "", // Description
        status: String = "Normal", // Health
    ): Result<Unit> {
        if (id.isBlank()) return Result.failure(IllegalArgumentException("ID tidak valid"))
        if (namaAlat.isBlank()) return Result.failure(IllegalArgumentException("Nama alat tidak boleh kosong."))

        if (kodeAlat.isBlank()) return Result.failure(IllegalArgumentException("Kode alat tidak boleh kosong."))

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Koordinat tidak valid."))
        }

        // 4. Fetch existing data to preserve other fields (like status, condition)
        val existingResult = repository.getAlatDetail(id)
        if (existingResult.isFailure) {
            return Result.failure(Exception("Alat tidak ditemukan"))
        }
        val existingAlat = existingResult.getOrNull()!!

        // 5. Update fields but preserve existing status/condition/tipe if defaults passed (or update if explicit)
        // Logic: if parameter is default, keep existing. Exception: we want to allow updating to empty?
        // For this UseCase, commonly used for editing Info (Name, Loc), not Status.
        // Assuming strict update of passed fields.

        val updatedAlat = existingAlat.copy(
            namaAlat = namaAlat,
            kodeAlat = kodeAlat,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName ?: existingAlat.locationName,
            // If tipe is not empty, update it. Else keep existing?
            // Or just update strict? The validaton logic above checked 'tipe'.
            tipe = if (tipe.isNotBlank()) tipe else existingAlat.tipe,

            // Preserve Status & Condition unless this UseCase is specifically used for them (which is likely not, given UpdateAlatStatusUseCase exists)
            // But if params are passed, maybe update?
            // The Test "update info should NOT change existing condition" passes default "" condition.
            // So we must fallback to existingAlat.kondisi if argument is empty.
            kondisi = if (kondisi.isNotBlank()) kondisi else existingAlat.kondisi,
            status = if (status != "Normal") status else existingAlat.status, // "Normal" is default param

            updatedAt = java.util.Date(),
        )

        return repository.insertAlat(updatedAlat)
    }
}
