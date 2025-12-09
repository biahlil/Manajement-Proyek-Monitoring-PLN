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
        val nameRegex = "^[a-zA-Z0-9 ]+$".toRegex()
        if (!nameRegex.matches(namaAlat)) {
            return Result.failure(IllegalArgumentException("Nama alat tidak boleh mengandung simbol."))
        }
        if (kodeAlat.isBlank()) return Result.failure(IllegalArgumentException("Kode alat tidak boleh kosong."))

        // nameRegex reused
        if (tipe.isNotBlank() && !nameRegex.matches(tipe)) {
            return Result.failure(IllegalArgumentException("Tipe alat tidak boleh mengandung simbol."))
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Koordinat tidak valid."))
        }

        // Use insertAlat (Upsert) to update all fields including condition and type
        val alat = com.pln.monitoringpln.domain.model.Alat(
            id = id,
            namaAlat = namaAlat,
            kodeAlat = kodeAlat,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            tipe = tipe,
            kondisi = kondisi,
            status = status,
            isArchived = false,
            updatedAt = java.util.Date(),
        )
        return repository.insertAlat(alat)
    }
}
