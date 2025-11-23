package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.Tugas

interface TugasRepository {
    suspend fun createTask(tugas: Tugas): Result<Unit>
    suspend fun getTasksByTeknisi(idTeknisi: String): Result<List<Tugas>>
    suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit>
    suspend fun getTaskDetail(taskId: String): Result<Tugas>
    suspend fun uploadTaskProof(taskId: String, photoBytes: ByteArray): Result<String>
}