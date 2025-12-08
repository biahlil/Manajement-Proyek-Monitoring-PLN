package com.pln.monitoringpln.data.mapper

import com.pln.monitoringpln.data.local.entity.TugasEntity
import com.pln.monitoringpln.data.model.TugasDto
import com.pln.monitoringpln.domain.model.Tugas

fun TugasEntity.toDomain(): Tugas {
    return Tugas(
        id = id,
        judul = judul,
        deskripsi = deskripsi,
        idAlat = idAlat,
        idTeknisi = idTeknisi,
        tglDibuat = tglDibuat,
        tglJatuhTempo = tglJatuhTempo,
        status = status,
        buktiFoto = buktiFoto,
        kondisiAkhir = kondisiAkhir,
    )
}

fun Tugas.toEntity(isSynced: Boolean = false): TugasEntity {
    return TugasEntity(
        id = id,
        judul = judul,
        deskripsi = deskripsi,
        idAlat = idAlat,
        idTeknisi = idTeknisi,
        tglDibuat = tglDibuat,
        tglJatuhTempo = tglJatuhTempo,
        status = status,
        buktiFoto = buktiFoto,
        kondisiAkhir = kondisiAkhir,
        isSynced = isSynced,
    )
}

fun TugasDto.toEntity(isSynced: Boolean = true): TugasEntity {
    return TugasEntity(
        id = id,
        judul = title,
        deskripsi = description ?: "",
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
        title = judul,
        description = deskripsi,
        status = status,
        dueDate = tglJatuhTempo,
        alatId = idAlat,
        teknisiId = idTeknisi,
    )
}
