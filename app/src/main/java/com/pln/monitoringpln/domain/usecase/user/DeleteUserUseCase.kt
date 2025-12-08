package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.repository.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return userRepository.deleteUser(id)
    }
}
