package com.pln.monitoringpln.domain.model

data class Alat(
    val id: String = "", // Kosong saat insert baru
    val kodeAlat: String,
    val namaAlat: String,
    val latitude: Double,
    val longitude: Double,
    val kondisi: String, // Di database akan di-set default, tapi di domain model harus ada
    val tipe: String = "Umum", // Tipe Peralatan

    // Field Baru untuk UC1b
    val status: String = "ACTIVE", // Default "ACTIVE", bisa jadi "PENDING_DELETE"
    val lastModifiedById: String? = null, // ID Teknisi terakhir yang update
    val isArchived: Boolean = false, // Soft Delete flag
    val locationName: String? = null // Reverse Geocoded Address
)
