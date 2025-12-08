package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.Alat

interface AlatRepository {
    // UC1a: Add
    suspend fun insertAlat(alat: Alat): Result<Unit>

    // UC1a: Get Detail (Admin & Teknisi)
    suspend fun getAlatDetail(id: String): Result<Alat>

    fun observeAlat(id: String): kotlinx.coroutines.flow.Flow<Alat?>

    // Get by Kode (Helper/QR Scan)
    suspend fun getAlatByKode(kode: String): Result<Alat>

    // UC1a: Update Info (Admin Only - Tanpa Kondisi)
    suspend fun updateAlatInfo(
        id: String,
        nama: String,
        kode: String,
        lat: Double,
        lng: Double,
        locationName: String? = null,
    ): Result<Unit>

    // UC1b: Soft Delete (Archive)
    suspend fun archiveAlat(id: String): Result<Unit>

    // UC6: Update Alat Condition (Teknisi Only)
    suspend fun updateAlatCondition(id: String, kondisi: String): Result<Unit>

    // Offline-First: Observe All Active Alat
    fun getAllAlat(): kotlinx.coroutines.flow.Flow<List<Alat>>

    // Sync Data
    suspend fun sync(): Result<Unit>


}
