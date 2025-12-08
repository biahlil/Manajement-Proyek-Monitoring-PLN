package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.TugasRepository
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase(private val repository: TugasRepository) {
    operator fun invoke(idTeknisi: String? = null): Flow<List<Tugas>> {
        return if (idTeknisi != null) {
            repository.observeTasksByTeknisi(idTeknisi)
        } else {
            repository.observeAllTasks()
        }
    }
}
