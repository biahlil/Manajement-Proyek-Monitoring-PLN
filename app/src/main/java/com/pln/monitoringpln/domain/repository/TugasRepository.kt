package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.Tugas

interface TugasRepository {
    suspend fun createTask(tugas: Tugas): Result<Unit>
}