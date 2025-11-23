package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository

class FakeTugasRepository : TugasRepository {

    // Simpan data di Memory List agar state terjaga selama tes berjalan
    val database = mutableListOf<Tugas>()

    override suspend fun createTask(tugas: Tugas): Result<Unit> {
        // Simulasi auto-generate ID jika kosong
        val newTugas = if (tugas.id.isEmpty()) {
            tugas.copy(id = "task-${System.currentTimeMillis()}-${database.size}")
        } else {
            tugas
        }
        database.add(newTugas)
        println("  ➡️ [FakeRepo] Insert tugas sukses: ${newTugas.deskripsi} (ID: ${newTugas.id})")
        return Result.success(Unit)
    }

    override suspend fun getTasksByTeknisi(idTeknisi: String): Result<List<Tugas>> {
        println("  ➡️ [FakeRepo] Mencari tugas untuk teknisi: $idTeknisi")
        val results = database.filter { it.idTeknisi == idTeknisi }
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

    override suspend fun getTaskDetail(taskId: String): Result<Tugas> {
        val task = database.find { it.id == taskId }
        return if (task != null) Result.success(task) else Result.failure(Exception("Tugas tidak ditemukan"))
    }

    override suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String> {
        println("  ➡️ [FakeRepo] Uploading ${photoBytes.size} bytes untuk Task $taskId...")
        // Simulasi sukses return URL dummy
        return Result.success("https://supabase-storage/bukti/$taskId.jpg")
    }

    // Helper untuk setup data awal di tes
    fun addDummyTasks(tasks: List<Tugas>) {
        database.addAll(tasks)
    }

    fun clear() {
        database.clear()
    }
}