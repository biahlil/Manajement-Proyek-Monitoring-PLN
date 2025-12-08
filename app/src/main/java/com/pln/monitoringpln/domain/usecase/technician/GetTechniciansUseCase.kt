package com.pln.monitoringpln.domain.usecase.technician

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetTechniciansUseCase(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<List<User>> {
        return repository.observeTeknisi()
    }

    suspend fun refresh(): Result<Unit> {
        return repository.refreshTeknisi()
    }
}
