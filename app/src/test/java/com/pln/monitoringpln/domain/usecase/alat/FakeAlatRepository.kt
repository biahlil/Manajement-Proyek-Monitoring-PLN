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
        return if (found != null) {
            println("     ✅ Data ditemukan: ${found.namaAlat}")
            Result.success(found)
        } else {
            println("     ❌ Data TIDAK ditemukan")
            Result.failure(Exception("Alat tidak ditemukan"))
        }
    }

    override suspend fun updateAlatInfo(id: String, nama: String, kode: String, lat: Double, lng: Double): Result<Unit> {
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
            // kondisi tidak disentuh!
        )
        println("     ✅ Update Berhasil. Kondisi setelah update: '${database[id]?.kondisi}'")
        return Result.success(Unit)
    }

    // --- UC1b: REQUEST DELETE (Baru) ---
    override suspend fun requestDeleteAlat(id: String): Result<Unit> {
        println("  ➡️ [FakeRepo] requestDeleteAlat() dipanggil. ID: $id")
        val existing = database[id] ?: return Result.failure(Exception("Alat tidak ditemukan"))

        // Ubah status jadi PENDING_DELETE
        database[id] = existing.copy(status = "PENDING_DELETE")
        println("     ✅ Status diubah menjadi PENDING_DELETE")
        return Result.success(Unit)
    }

    // --- UC1b: DELETE PERMANENT (Baru) ---
    override suspend fun deleteAlat(id: String): Result<Unit> {
        println("  ➡️ [FakeRepo] deleteAlat() dipanggil (Hapus Permanen). ID: $id")
        if (!database.containsKey(id)) {
            println("     ❌ Gagal: ID tidak ditemukan")
            return Result.failure(Exception("Alat tidak ditemukan"))
        }
        database.remove(id)
        println("     ✅ Data dihapus permanen dari memory.")
        return Result.success(Unit)
    }

    fun addDummy(alat: Alat) {
        println("  🔧 [Setup] Menambahkan dummy data: ${alat.namaAlat} (ID: ${alat.id})")
        database[alat.id] = alat
    }

    fun clear() { database.clear() }
}
