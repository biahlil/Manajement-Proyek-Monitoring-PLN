package com.pln.monitoringpln.data.model

import com.pln.monitoringpln.domain.model.Alat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlatDto(
    @SerialName("id") val id: String,
    @SerialName("kode_alat") val kodeAlat: String,
    @SerialName("nama_alat") val namaAlat: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("kondisi") val kondisi: String,
    @SerialName("status") val status: String,
    @SerialName("last_modified_by_id") val lastModifiedById: String? = null,
    @SerialName("is_archived") val isArchived: Boolean = false,
)

@Serializable
data class AlatInsertDto(
    @SerialName("id") val id: String,
    @SerialName("kode_alat") val kodeAlat: String,
    @SerialName("nama_alat") val namaAlat: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("kondisi") val kondisi: String,
    @SerialName("status") val status: String,
    @SerialName("is_archived") val isArchived: Boolean = false,
)

fun Alat.toInsertDto(): AlatInsertDto {
    return AlatInsertDto(
        id = id,
        kodeAlat = kodeAlat,
        namaAlat = namaAlat,
        latitude = latitude,
        longitude = longitude,
        kondisi = kondisi,
        status = status,
        isArchived = isArchived,
    )
}

@Serializable
data class AlatUpdateInfoDto(
    @SerialName("nama_alat") val namaAlat: String,
    @SerialName("kode_alat") val kodeAlat: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
)

@Serializable
data class AlatArchiveDto(
    @SerialName("status") val status: String,
    @SerialName("is_archived") val isArchived: Boolean,
)

@Serializable
data class AlatConditionDto(
    @SerialName("kondisi") val kondisi: String,
)
