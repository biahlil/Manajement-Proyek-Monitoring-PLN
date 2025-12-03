package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.UserRepository

class GetUserProfileUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))

        return userRepository.getTeknisiDetail(userId)
    }
}
