package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.exception.ValidationException
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository

class UpdateUserProfileUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        if (user.namaLengkap.isBlank()) {
            return Result.failure(ValidationException("Nama tidak boleh kosong"))
        }
        val nameRegex = "^[a-zA-Z0-9 ]+$".toRegex()
        if (!nameRegex.matches(user.namaLengkap)) {
            return Result.failure(ValidationException("Nama tidak boleh mengandung simbol"))
        }
        if (user.email.isBlank()) {
            return Result.failure(ValidationException("Email tidak boleh kosong"))
        }
        val emailRegex = "^[A-Za-z0-9._%+-]+@(pln\\.co\\.id|gmail\\.com)$".toRegex()
        if (!emailRegex.matches(user.email)) {
            return Result.failure(ValidationException("Email harus pln.co.id atau gmail.com"))
        }
        return userRepository.updateUser(user)
    }
}
