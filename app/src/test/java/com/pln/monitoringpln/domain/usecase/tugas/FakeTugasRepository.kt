package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository
import java.text.SimpleDateFormat
import java.util.Locale

class FakeTugasRepository : TugasRepository {

    // Formatter tanggal untuk pencarian string tanggal
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Simpan data di Memory List agar state terjaga selama tes berjalan
    val database = mutableListOf<Tugas>()

    override suspend fun createTask(tugas: Tugas): Result<Tugas?> {
        // Simulasi auto-generate ID jika kosong
        val newTugas = if (tugas.id.isEmpty()) {
            tugas.copy(id = "task-${System.currentTimeMillis()}-${database.size}")
        } else {
            tugas
        }
        database.add(newTugas)
        println("  ➡️ [FakeRepo] Insert tugas sukses: ${newTugas.deskripsi} (ID: ${newTugas.id})")
        return Result.success(newTugas)
    }

    override suspend fun getTasksByTeknisi(idTeknisi: String, searchQuery: String?): Result<List<Tugas>> {
        println("  ➡️ [FakeRepo] Cari tugas teknisi: $idTeknisi, Query: $searchQuery")

        // 1. Filter by Teknisi (Wajib)
        var results = database.filter { it.idTeknisi == idTeknisi }

        // 2. Filter by Universal Search (Opsional)
        if (!searchQuery.isNullOrBlank()) {
            val query = searchQuery.lowercase()
            results = results.filter { tugas ->
                // Cek Deskripsi/Title
                val matchDeskripsi = tugas.deskripsi.lowercase().contains(query)

                // Cek Kode Alat (ID Alat)
                val matchAlat = tugas.idAlat.lowercase().contains(query)

                // Cek Status
                val matchStatus = tugas.status.lowercase().contains(query)

                // Cek Tanggal (Dibuat & Deadline) - Konversi ke String dulu
                // *Catatan: Di implementasi asli (SQL), ini menggunakan to_char() atau casting
                val tglDibuatStr = dateFormatter.format(tugas.tglDibuat).lowercase()
                val tglJatuhTempoStr = dateFormatter.format(tugas.tglJatuhTempo).lowercase()

                val matchTglDibuat = tglDibuatStr.contains(query)
                val matchDeadline = tglJatuhTempoStr.contains(query)

                // LOGIKA OR: Salah satu cocok, maka ambil
                matchDeskripsi || matchAlat || matchStatus || matchTglDibuat || matchDeadline
            }
        }

        println("  ➡️ [FakeRepo] Ditemukan: ${results.size} tugas")
        return Result.success(results)
    }
    override suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit> {
        println("  ➡️ [FakeRepo] Request update status: ID $taskId jadi '$newStatus'")

        val index = database.indexOfFirst { it.id == taskId }
        if (index == -1) {
            println("     ❌ Gagal: ID Tugas tidak ditemukan")
            return Result.failure(Exception("Tugas tidak ditemukan"))
        }

        val existingTask = database[index]
        database[index] = existingTask.copy(status = newStatus)

        println("     ✅ Sukses update status")
        return Result.success(Unit)
    }

    override suspend fun getTasksByAlat(idAlat: String): Result<List<Tugas>> {
        println("  ➡️ [FakeRepo] Cari riwayat tugas untuk Alat: $idAlat")
        val results = database.filter { it.idAlat == idAlat }
        // Opsional: Urutkan dari yang terbaru (descending date)
        val sorted = results.sortedByDescending { it.tglDibuat }

        println("  ➡️ [FakeRepo] Ditemukan: ${sorted.size} tugas")
        return Result.success(sorted)
    }

    override suspend fun getTaskDetail(taskId: String): Result<Tugas> {
        val task = database.find { it.id == taskId }
        return if (task != null) Result.success(task) else Result.failure(Exception("Tugas tidak ditemukan"))
    }

    override suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String> {
        println("  ➡️ [FakeRepo] Uploading ${photoBytes.size} bytes untuk Task $taskId...")
        // Simulasi sukses return URL dummy
        return Result.success("https://supabase-storage/bukti/$taskId.jpg")
    }

    override suspend fun completeTugas(id: String, buktiFotoPath: String, kondisiAkhir: String): Result<Unit> {
        println("  ➡️ [FakeRepo] Completing task $id with proof $buktiFotoPath and condition $kondisiAkhir")
        val index = database.indexOfFirst { it.id == id }
        if (index != -1) {
            database[index] = database[index].copy(status = "Done")
            return Result.success(Unit)
        }
        return Result.failure(Exception("Task not found"))
    }

    override suspend fun sync(): Result<Unit> {
        println("  ➡️ [FakeRepo] Sync triggered")
        return Result.success(Unit)
    }

    // Helper untuk setup data awal di tes
    fun addDummyTasks(tasks: List<Tugas>) {
        database.addAll(tasks)
    }

    fun clear() {
        database.clear()
    }
}
