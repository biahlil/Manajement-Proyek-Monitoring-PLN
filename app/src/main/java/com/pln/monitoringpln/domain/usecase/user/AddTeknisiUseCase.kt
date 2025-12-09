package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.AuthRepository

class AddTeknisiUseCase(private val repository: AuthRepository) {

    // Regex yang mendukung subdomain
    private val emailRegex = "^[A-Za-z0-9._%+-]+@(pln\\.co\\.id|gmail\\.com)$".toRegex()

    suspend operator fun invoke(
        namaLengkap: String,
        email: String,
        password: String,
        photoUrl: String? = null
    ): Result<Unit> {
        if (namaLengkap.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama tidak boleh kosong."))
        }
        val nameRegex = "^[a-zA-Z0-9 ]+$".toRegex()
        if (!nameRegex.matches(namaLengkap)) {
            return Result.failure(IllegalArgumentException("Nama tidak boleh mengandung simbol."))
        }

        if (!email.matches(emailRegex)) {
            return Result.failure(IllegalArgumentException("Email harus pln.co.id atau gmail.com"))
        }

        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password minimal 6 karakter."))
        }

        return repository.createUser(email, password, namaLengkap, "TEKNISI", photoUrl)
    }
}
