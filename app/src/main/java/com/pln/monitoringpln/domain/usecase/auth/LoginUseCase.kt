package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    suspend operator fun invoke(email: String, password: String): Result<Unit> {

    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        // 1. Validasi Format
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email tidak boleh kosong."))
        if (!email.matches(emailRegex)) return Result.failure(IllegalArgumentException("Format email tidak valid."))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password tidak boleh kosong."))

        // 2. Panggil Repository (Supabase Auth)
        return authRepository.login(email, password)
    }

    fun isUserLoggedIn(): kotlinx.coroutines.flow.Flow<Boolean> {
        return authRepository.isUserLoggedIn()
    }

    suspend fun loadSession() {
        authRepository.loadSession()
    }
}
