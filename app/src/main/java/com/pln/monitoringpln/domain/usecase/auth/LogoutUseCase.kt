package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
