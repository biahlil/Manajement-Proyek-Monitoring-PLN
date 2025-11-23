package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository

class GetMyTasksUseCase(private val repository: TugasRepository) {

    // Parameter searchQuery opsional (default null)
    suspend operator fun invoke(idTeknisi: String, searchQuery: String? = null): Result<List<Tugas>> {
        if (idTeknisi.isBlank()) {
            return Result.failure(IllegalArgumentException("ID Teknisi tidak boleh kosong."))
        }

        // Kita serahkan query ke repository untuk diproses (filtering di Data Layer)
        return repository.getTasksByTeknisi(idTeknisi, searchQuery)
    }
}
