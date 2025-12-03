package com.pln.monitoringpln.data.mapper

import com.pln.monitoringpln.data.local.entity.AlatEntity
import com.pln.monitoringpln.domain.model.Alat

fun AlatEntity.toDomain(): Alat {
    return Alat(
        id = id,
        namaAlat = namaAlat,
        kodeAlat = kodeAlat,
        latitude = latitude,
        longitude = longitude,
        kondisi = kondisi,
        status = status,
        tipe = tipe,
        lastModifiedById = lastModifiedById,
        isArchived = isArchived,
        locationName = locationName
    )
}

fun Alat.toEntity(isSynced: Boolean = true): AlatEntity {
    return AlatEntity(
        id = id,
        namaAlat = namaAlat,
        kodeAlat = kodeAlat,
        latitude = latitude,
        longitude = longitude,
        kondisi = kondisi,
        status = status,
        tipe = tipe,
        lastModifiedById = lastModifiedById,
        isSynced = isSynced,
        isArchived = false, // Default to false when converting from domain
        locationName = locationName
    )
}

fun com.pln.monitoringpln.data.model.AlatDto.toDomain(): Alat {
    return Alat(
        id = id,
        namaAlat = namaAlat,
        kodeAlat = kodeAlat,
        latitude = latitude,
        longitude = longitude,
        kondisi = kondisi,
        status = status,
        tipe = tipe ?: "Umum",
        lastModifiedById = lastModifiedById,
        locationName = locationName
    )
}
