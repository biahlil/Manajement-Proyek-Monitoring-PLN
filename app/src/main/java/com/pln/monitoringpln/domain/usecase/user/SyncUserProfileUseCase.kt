package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.repository.UserRepository

class SyncUserProfileUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.syncProfile()
    }
}
