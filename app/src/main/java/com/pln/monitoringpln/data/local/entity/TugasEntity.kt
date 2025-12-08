package com.pln.monitoringpln.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tugas")
data class TugasEntity(
    @PrimaryKey
    val id: String,
    val judul: String,
    val deskripsi: String,
    val idAlat: String,
    val idTeknisi: String,
    val tglDibuat: Date,
    val tglJatuhTempo: Date,
    val status: String,
    val buktiFoto: String? = null,
    val kondisiAkhir: String? = null,
    val isSynced: Boolean = true,
    val isArchived: Boolean = false,
)
