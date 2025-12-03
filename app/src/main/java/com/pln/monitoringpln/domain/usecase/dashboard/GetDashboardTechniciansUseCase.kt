package com.pln.monitoringpln.domain.usecase.dashboard

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetDashboardTechniciansUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> {
        return userRepository.observeTeknisi().map { users ->
            users.take(3)
        }
    }

    suspend fun refresh(): Result<Unit> {
        return userRepository.refreshTeknisi()
    }
}
