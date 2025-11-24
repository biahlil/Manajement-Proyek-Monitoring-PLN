package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.Alat

interface AlatRepository {
    // UC1a: Add
    suspend fun insertAlat(alat: Alat): Result<Unit>

    // UC1a: Get Detail (Admin & Teknisi)
    suspend fun getAlatDetail(id: String): Result<Alat>

    // UC1a: Update Info (Admin Only - Tanpa Kondisi)
    suspend fun updateAlatInfo(
        id: String,
        nama: String,
        kode: String,
        lat: Double,
        lng: Double,
    ): Result<Unit>

    // UC1b
    suspend fun requestDeleteAlat(id: String): Result<Unit>
    suspend fun deleteAlat(id: String): Result<Unit>

    // UC6: Update Alat Condition (Teknisi Only)
    suspend fun updateAlatCondition(id: String, kondisi: String): Result<Unit>
}
