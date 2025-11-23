package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository
import java.lang.IllegalArgumentException

class AddTeknisiUseCase(private val repository: UserRepository) {

    // Regex yang mendukung subdomain
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    suspend operator fun invoke(namaLengkap: String, email: String, password: String): Result<User> {
        if (namaLengkap.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama tidak boleh kosong."))
        }

        if (!email.matches(emailRegex)) {
            return Result.failure(IllegalArgumentException("Format email tidak valid."))
        }

        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password minimal 6 karakter."))
        }

        return repository.addTeknisi(email, password, namaLengkap)
    }
}
