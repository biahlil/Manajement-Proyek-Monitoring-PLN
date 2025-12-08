package com.pln.monitoringpln.data.model

import com.pln.monitoringpln.domain.model.Tugas
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class TugasDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("status") val status: String,
    @SerialName("due_date") @Serializable(with = DateSerializer::class) val dueDate: Date,
    @SerialName("alat_id") val alatId: String,
    @SerialName("teknisi_id") val teknisiId: String,
    @SerialName("created_at") @Serializable(with = DateSerializer::class) val createdAt: Date,
)

fun TugasDto.toDomain(): Tugas {
    return Tugas(
        id = id,
        deskripsi = title, // Mapping title back to deskripsi
        idAlat = alatId,
        idTeknisi = teknisiId,
        tglDibuat = createdAt,
        tglJatuhTempo = dueDate,
        status = status,
    )
}

fun Tugas.toDto(): TugasDto {
    return TugasDto(
        id = id,
        title = deskripsi,
        description = deskripsi, // Use deskripsi for description too
        status = status,
        dueDate = tglJatuhTempo,
        alatId = idAlat,
        teknisiId = idTeknisi,
        createdAt = tglDibuat,
    )
}

@Serializable
data class TugasInsertDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("status") val status: String,
    @SerialName("due_date") @Serializable(with = DateSerializer::class) val dueDate: Date,
    @SerialName("alat_id") val alatId: String,
    @SerialName("teknisi_id") val teknisiId: String,
)

fun Tugas.toInsertDto(): TugasInsertDto {
    return TugasInsertDto(
        id = id,
        title = deskripsi,
        description = deskripsi,
        status = status,
        dueDate = tglJatuhTempo,
        alatId = idAlat,
        teknisiId = idTeknisi,
    )
}

@Serializable
data class TugasUpdateStatusDto(
    @SerialName("status") val status: String,
)
