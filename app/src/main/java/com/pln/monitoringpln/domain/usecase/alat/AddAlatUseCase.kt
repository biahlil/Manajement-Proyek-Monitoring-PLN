package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository
import java.lang.IllegalArgumentException

class AddAlatUseCase(
    private val repository: AlatRepository,
) {
    // Parameter sesuai input UI Admin
    suspend operator fun invoke(
        id: String = java.util.UUID.randomUUID().toString(),
        namaAlat: String,
        kodeAlat: String,
        latitude: Double,
        longitude: Double,
        locationName: String? = null,
        tipe: String = "",
        kondisi: String = "", // Description
        status: String = "Normal", // Health
    ): Result<Unit> {
        // 1. Validasi Dasar
        if (namaAlat.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama alat tidak boleh kosong."))
        }
        val nameRegex = "^[a-zA-Z0-9 ]+$".toRegex()
        if (!nameRegex.matches(namaAlat)) {
            return Result.failure(IllegalArgumentException("Nama alat tidak boleh mengandung simbol."))
        }
        if (kodeAlat.isBlank()) {
            return Result.failure(IllegalArgumentException("Kode alat tidak boleh kosong."))
        }
        if (tipe.isNotBlank() && !nameRegex.matches(tipe)) {
            return Result.failure(IllegalArgumentException("Tipe alat tidak boleh mengandung simbol."))
        }

        // 2. Validasi Geospasial (Koordinat Valid Bumi)
        // Lat: -90 s/d 90, Lng: -180 s/d 180
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Koordinat tidak valid."))
        }

        // 3. Buat Model
        val newAlat = Alat(
            id = id,
            kodeAlat = kodeAlat,
            namaAlat = namaAlat,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            kondisi = kondisi,
            tipe = tipe,
            status = status,
            isArchived = false,
            updatedAt = java.util.Date(),
        )

        return repository.insertAlat(newAlat)
    }
}
