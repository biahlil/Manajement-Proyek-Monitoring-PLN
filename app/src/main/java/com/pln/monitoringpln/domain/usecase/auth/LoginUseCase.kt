package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository

private val EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
class LoginUseCase(
    private val userRepository: UserRepository, // Bergantung pada Interface
) {
    // Kita gunakan 'invoke' agar kelas ini bisa dipanggil seperti fungsi
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            // Pesan spesifik untuk email kosong
            return Result.failure(IllegalArgumentException("Email tidak boleh kosong."))
        }

        if (!email.matches(EMAIL_REGEX)) {
            // Pesan spesifik untuk format
            return Result.failure(IllegalArgumentException("Format email tidak valid."))
        }

        if (password.isBlank()) {
            // Pesan spesifik untuk password
            return Result.failure(IllegalArgumentException("Password tidak boleh kosong."))
        }

        // Panggil repository dan kembalikan hasilnya
        return userRepository.login(email, password)
    }
}
