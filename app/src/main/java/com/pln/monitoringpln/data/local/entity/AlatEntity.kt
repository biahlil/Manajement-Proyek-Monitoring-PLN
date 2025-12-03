package com.pln.monitoringpln.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alat")
data class AlatEntity(
    @PrimaryKey
    val id: String,
    val kodeAlat: String,
    val namaAlat: String,
    val latitude: Double,
    val longitude: Double,
    val kondisi: String,
    val tipe: String = "Umum",
    val status: String,
    val lastModifiedById: String?,
    val isArchived: Boolean = false, // Soft Delete
    val isSynced: Boolean = true, // Offline Sync
    val locationName: String? = null
)
