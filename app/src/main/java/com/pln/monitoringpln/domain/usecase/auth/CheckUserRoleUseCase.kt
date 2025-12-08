package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.repository.AuthRepository

class CheckUserRoleUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<String> {
        return authRepository.getUserRole()
    }
}
