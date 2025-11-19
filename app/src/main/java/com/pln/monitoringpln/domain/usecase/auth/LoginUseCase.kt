package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository,
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // 1. Validasi Format
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email tidak boleh kosong."))
        if (!email.matches(emailRegex)) return Result.failure(IllegalArgumentException("Format email tidak valid."))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password tidak boleh kosong."))

        // 2. Panggil Repository
        val result = userRepository.login(email, password)

        // 3. [BARU] Cek Status Aktif (Soft Delete)
        if (result.isSuccess) {
            val user = result.getOrNull()
            if (user != null && !user.isActive) {
                return Result.failure(Exception("Akun Anda telah dinonaktifkan. Hubungi Admin."))
            }
        }

        return result
    }
}