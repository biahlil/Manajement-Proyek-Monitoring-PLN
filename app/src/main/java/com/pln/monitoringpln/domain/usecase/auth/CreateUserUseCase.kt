package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.repository.AuthRepository

class CreateUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        role: String,
        photoUrl: String? = null
    ): Result<Unit> {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            return Result.failure(IllegalArgumentException("All fields must be filled"))
        }
        if (role != "ADMIN" && role != "TEKNISI") {
            return Result.failure(IllegalArgumentException("Invalid role: $role"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        return authRepository.createUser(email, password, fullName, role, photoUrl)
    }
}
