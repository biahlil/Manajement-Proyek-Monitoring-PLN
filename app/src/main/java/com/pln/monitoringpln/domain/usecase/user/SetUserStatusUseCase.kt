package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.repository.UserRepository
import java.lang.IllegalArgumentException

class SetUserStatusUseCase(private val repository: UserRepository) {

    suspend operator fun invoke(userId: String, isActive: Boolean): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID User tidak valid."))
        }
        return repository.setUserStatus(userId, isActive)
    }
}