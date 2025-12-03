package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.repository.AlatRepository

// --- FAKE REPOSITORY (Untuk Semua Tes) ---
class FakeAlatRepository : AlatRepository {
    // In-Memory Database
    val database = mutableMapOf<String, Alat>()
    var lastSavedAlat: Alat? = null // Untuk verifikasi insert

    override suspend fun insertAlat(alat: Alat): Result<Unit> {
        println("  ➡️ [FakeRepo] insertAlat() dipanggil. Data: ${alat.namaAlat}, Kondisi: ${alat.kondisi}")
        lastSavedAlat = alat
        val id = if (alat.id.isBlank()) "gen-${System.currentTimeMillis()}" else alat.id
        database[id] = alat.copy(id = id)
        return Result.success(Unit)
    }

    override suspend fun getAlatDetail(id: String): Result<Alat> {
        println("  ➡️ [FakeRepo] getAlatDetail() dipanggil. ID: $id")
        val found = database[id]
        return if (found != null && !found.isArchived) {
            println("     ✅ Data ditemukan: ${found.namaAlat}")
            Result.success(found)
        } else {
            println("     ❌ Data TIDAK ditemukan")
            Result.failure(Exception("Alat tidak ditemukan"))
        }
    }

    override suspend fun getAlatByKode(kode: String): Result<Alat> {
        println("  ➡️ [FakeRepo] getAlatByKode() dipanggil. Kode: $kode")
        val found = database.values.find { it.kodeAlat == kode }
        return if (found != null && !found.isArchived) {
            Result.success(found)
        } else {
            Result.failure(Exception("Alat tidak ditemukan"))
        }
    }

    override suspend fun updateAlatInfo(id: String, nama: String, kode: String, lat: Double, lng: Double, locationName: String?): Result<Unit> {
        println("  ➡️ [FakeRepo] updateAlatInfo() dipanggil. ID: $id, Nama Baru: $nama")
        val existing = database[id]

        if (existing == null) {
            println("     ❌ Gagal Update: ID tidak ditemukan")
            return Result.failure(Exception("Alat tidak ditemukan"))
        }

        // Logika simulasi update
        println("     ℹ️ Kondisi sebelum update: '${existing.kondisi}'")
        database[id] = existing.copy(
            namaAlat = nama,
            kodeAlat = kode,
            latitude = lat,
            longitude = lng,
            locationName = locationName
            // kondisi tidak disentuh!
        )
        println("     ✅ Update Berhasil. Kondisi setelah update: '${database[id]?.kondisi}'")
        return Result.success(Unit)
    }

    override fun observeAlat(id: String): kotlinx.coroutines.flow.Flow<Alat?> {
        return kotlinx.coroutines.flow.flowOf(database[id])
    }

    // --- UC1b: SOFT DELETE (ARCHIVE) ---
    override suspend fun archiveAlat(id: String): Result<Unit> {
        println("  ➡️ [FakeRepo] archiveAlat() dipanggil. ID: $id")
        val existing = database[id] ?: return Result.failure(Exception("Alat tidak ditemukan"))

        // Ubah status jadi ARCHIVED / isArchived = true
        database[id] = existing.copy(isArchived = true, status = "ARCHIVED")
        println("     ✅ Status diubah menjadi ARCHIVED")
        return Result.success(Unit)
    }

    override suspend fun updateAlatCondition(id: String, kondisi: String): Result<Unit> {
        println("  ➡️ [FakeRepo] Update kondisi Alat $id menjadi '$kondisi'")
        val existing = database[id] ?: return Result.failure(Exception("Alat tidak ditemukan"))

        database[id] = existing.copy(kondisi = kondisi)
        return Result.success(Unit)
    }

    override fun getAllAlat(): kotlinx.coroutines.flow.Flow<List<Alat>> {
        return kotlinx.coroutines.flow.flowOf(database.values.toList())
    }

    override suspend fun sync(): Result<Unit> {
        println("  ➡️ [FakeRepo] Sync triggered")
        return Result.success(Unit)
    }

    fun addDummy(alat: Alat) {
        println("  🔧 [Setup] Menambahkan dummy data: ${alat.namaAlat} (ID: ${alat.id})")
        database[alat.id] = alat
    }

    fun clear() { database.clear() }
}
