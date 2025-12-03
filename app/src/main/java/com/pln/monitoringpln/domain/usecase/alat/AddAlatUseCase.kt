package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import java.lang.IllegalArgumentException

class AddAlatUseCase(
    private val repository: AlatRepository,
) {
    // Parameter sesuai input UI Admin (tanpa kondisi)
    suspend operator fun invoke(
        namaAlat: String,
        kodeAlat: String,
        latitude: Double,
        longitude: Double,
        locationName: String? = null
    ): Result<Unit> {
        // 1. Validasi Dasar
        if (namaAlat.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama alat tidak boleh kosong."))
        }
        if (kodeAlat.isBlank()) {
            return Result.failure(IllegalArgumentException("Kode alat tidak boleh kosong."))
        }

        // 2. Validasi Geospasial (Koordinat Valid Bumi)
        // Lat: -90 s/d 90, Lng: -180 s/d 180
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Koordinat tidak valid."))
        }

        // 3. Buat Model (Enforce Default Condition)
        val newAlat = Alat(
            kodeAlat = kodeAlat,
            namaAlat = namaAlat,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            kondisi = "Normal", // <-- Hardcoded Default sesuai User Story
        )

        return repository.insertAlat(newAlat)
    }
}
