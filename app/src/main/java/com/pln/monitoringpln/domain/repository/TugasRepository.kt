package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.Tugas

interface TugasRepository {
    suspend fun createTask(tugas: Tugas): Result<Tugas?>
    suspend fun getTasksByTeknisi(idTeknisi: String, searchQuery: String? = null): Result<List<Tugas>>
    suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit>
    suspend fun completeTugas(id: String, buktiFotoPath: String, kondisiAkhir: String): Result<Unit>
    suspend fun sync(): Result<Unit>
    suspend fun getTaskDetail(taskId: String): Result<Tugas>
    suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String>
    suspend fun getTasksByAlat(idAlat: String): Result<List<Tugas>>
    fun observeTasksByAlat(idAlat: String): kotlinx.coroutines.flow.Flow<List<Tugas>>
    fun observeAllTasks(): kotlinx.coroutines.flow.Flow<List<Tugas>>
    fun observeTasksByTeknisi(idTeknisi: String): kotlinx.coroutines.flow.Flow<List<Tugas>>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun updateTask(tugas: Tugas): Result<Unit>
}
