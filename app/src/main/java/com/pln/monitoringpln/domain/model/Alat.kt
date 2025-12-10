package com.pln.monitoringpln.domain.model
import java.util.Date

data class Alat(
    val id: String = "",
    val kodeAlat: String,
    val namaAlat: String,
    val latitude: Double,
    val longitude: Double,
    val kondisi: String,
    val tipe: String = "Umum",
    val status: String = "NORMAL",
    val lastModifiedById: String? = null,
    val isArchived: Boolean = false,
    val locationName: String? = null,
    val updatedAt: Date? = null,
)
