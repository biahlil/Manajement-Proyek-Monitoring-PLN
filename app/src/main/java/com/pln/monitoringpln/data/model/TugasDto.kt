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
    @SerialName("bukti_foto") val buktiFoto: String? = null,
    @SerialName("kondisi_akhir") val kondisiAkhir: String? = null,
)

fun TugasDto.toDomain(): Tugas {
    return Tugas(
        id = id,
        judul = title,
        deskripsi = description ?: "",
        idAlat = alatId,
        idTeknisi = teknisiId,
        tglDibuat = createdAt,
        tglJatuhTempo = dueDate,
        status = status,
        buktiFoto = buktiFoto,
        kondisiAkhir = kondisiAkhir,
    )
}

fun Tugas.toDto(): TugasDto {
    return TugasDto(
        id = id,
        title = judul,
        description = deskripsi,
        status = status,
        dueDate = tglJatuhTempo,
        alatId = idAlat,
        teknisiId = idTeknisi,
        createdAt = tglDibuat,
        buktiFoto = buktiFoto,
        kondisiAkhir = kondisiAkhir,
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
    @SerialName("bukti_foto") val buktiFoto: String? = null,
    @SerialName("kondisi_akhir") val kondisiAkhir: String? = null,
)

fun Tugas.toInsertDto(): TugasInsertDto {
    return TugasInsertDto(
        id = id,
        title = judul,
        description = deskripsi,
        status = status,
        dueDate = tglJatuhTempo,
        alatId = idAlat,
        teknisiId = idTeknisi,
        buktiFoto = buktiFoto,
        kondisiAkhir = kondisiAkhir,
    )
}

@Serializable
data class TugasUpdateStatusDto(
    @SerialName("status") val status: String,
)
