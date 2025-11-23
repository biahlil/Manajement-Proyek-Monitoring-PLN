package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository

class GetMyTasksUseCase(private val repository: TugasRepository) {

    suspend operator fun invoke(idTeknisi: String): Result<List<Tugas>> {
        if (idTeknisi.isBlank()) {
            return Result.failure(Exception("ID Teknisi tidak boleh kosong."))
        }

        return repository.getTasksByTeknisi(idTeknisi)
    }
}