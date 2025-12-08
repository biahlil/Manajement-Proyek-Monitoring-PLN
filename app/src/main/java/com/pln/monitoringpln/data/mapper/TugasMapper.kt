package com.pln.monitoringpln.data.mapper

import com.pln.monitoringpln.data.local.entity.TugasEntity
import com.pln.monitoringpln.data.model.TugasDto
import com.pln.monitoringpln.domain.model.Tugas

fun TugasEntity.toDomain(): Tugas {
    return Tugas(
        id = id,
        deskripsi = deskripsi,
        idAlat = idAlat,
        idTeknisi = idTeknisi,
        tglDibuat = tglDibuat,
        tglJatuhTempo = tglJatuhTempo,
        status = status,
    )
}

fun Tugas.toEntity(isSynced: Boolean = false): TugasEntity {
    return TugasEntity(
        id = id,
        deskripsi = deskripsi,
        idAlat = idAlat,
        idTeknisi = idTeknisi,
        tglDibuat = tglDibuat,
        tglJatuhTempo = tglJatuhTempo,
        status = status,
        isSynced = isSynced,
    )
}

fun TugasDto.toEntity(isSynced: Boolean = true): TugasEntity {
    return TugasEntity(
        id = id,
        deskripsi = title, // Mapping title to deskripsi
        idAlat = alatId,
        idTeknisi = teknisiId,
        tglDibuat = createdAt,
        tglJatuhTempo = dueDate,
        status = status,
        isSynced = isSynced,
    )
}

fun Tugas.toInsertDto(): com.pln.monitoringpln.data.model.TugasInsertDto {
    return com.pln.monitoringpln.data.model.TugasInsertDto(
        id = id,
        title = deskripsi,
        description = deskripsi,
        status = status,
        dueDate = tglJatuhTempo,
        alatId = idAlat,
        teknisiId = idTeknisi,
    )
}
