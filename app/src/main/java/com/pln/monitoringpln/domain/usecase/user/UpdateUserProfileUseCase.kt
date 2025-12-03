package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository

import com.pln.monitoringpln.domain.exception.ValidationException

class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        if (user.namaLengkap.isBlank()) {
            return Result.failure(ValidationException("Nama tidak boleh kosong"))
        }
        if (user.email.isBlank()) {
            return Result.failure(ValidationException("Email tidak boleh kosong"))
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!emailRegex.matches(user.email)) {
            return Result.failure(ValidationException("Format email tidak valid"))
        }
        return userRepository.updateUser(user)
    }
}
