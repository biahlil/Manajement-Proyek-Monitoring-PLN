package com.pln.monitoringpln.utils

import com.pln.monitoringpln.data.local.entity.AlatEntity

object TestObjects {
    fun createAlat(
        id: String = "1",
        kodeAlat: String = "A1",
        namaAlat: String = "Trafo Test",
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        kondisi: String = "Baik",
        status: String = "ACTIVE",
        lastModifiedById: String? = null,
        isArchived: Boolean = false
    ): AlatEntity {
        return AlatEntity(
            id = id,
            kodeAlat = kodeAlat,
            namaAlat = namaAlat,
            latitude = latitude,
            longitude = longitude,
            kondisi = kondisi,
            status = status,
            lastModifiedById = lastModifiedById,
            isArchived = isArchived
        )
    }

    // Data User untuk Integration Test
    val ADMIN_USER_EMAIL = "boss@pln.co.id" // Pastikan user ini ada di Supabase
    val ADMIN_USER_PASSWORD = "password123"
}
