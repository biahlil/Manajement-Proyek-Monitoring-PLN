package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.repository.AuthRepository

import com.pln.monitoringpln.domain.exception.ValidationException

class UpdatePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(password: String, confirmPassword: String): Result<Unit> {
        if (password.isBlank()) {
            return Result.failure(ValidationException("Password tidak boleh kosong"))
        }
        if (password.length < 6) {
            return Result.failure(ValidationException("Password minimal 6 karakter"))
        }
        if (password != confirmPassword) {
            return Result.failure(ValidationException("Password tidak cocok"))
        }
        return authRepository.updatePassword(password)
    }
}
