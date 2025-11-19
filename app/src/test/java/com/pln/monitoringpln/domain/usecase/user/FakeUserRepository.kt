package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository

class FakeUserRepository : UserRepository {
    val users = mutableMapOf<String, User>()

    override suspend fun login(email: String, password: String): Result<User> {
        println("  ➡️ [FakeUserRepo] login() dipanggil. Email: $email")

        // 1. Cari user by email
        val user = users.values.find { it.email == email }

        if (user == null) {
            println("     ❌ Gagal: Email tidak ditemukan")
            return Result.failure(Exception("Kredensial salah"))
        }

        // 2. Simulasi Cek Password (Sederhana: hardcode "password123")
        // Di real app, ini dilakukan oleh Supabase Auth
        if (password == "password123") {
            println("     ✅ Login Sukses: ${user.namaLengkap}")
            return Result.success(user)
        } else {
            println("     ❌ Gagal: Password salah")
            return Result.failure(Exception("Kredensial salah"))
        }
    }

    override suspend fun addTeknisi(email: String, password: String, namaLengkap: String): Result<User> {
        val newUser = User("gen-${System.currentTimeMillis()}", email, namaLengkap, "Teknisi")
        users[newUser.id] = newUser
        return Result.success(newUser)
    }

    override suspend fun getTeknisiDetail(id: String): Result<User> {
        println("  ➡️ [FakeUserRepo] Cari User ID: $id")
        val user = users[id]
        return if (user != null) {
            println("     ✅ User Ditemukan: ${user.namaLengkap} (${user.role})")
            Result.success(user)
        } else {
            println("     ❌ User Tidak Ditemukan")
            Result.failure(Exception("User tidak ditemukan"))
        }
    }

    override suspend fun setUserStatus(id: String, isActive: Boolean): Result<Unit> {
        println("  ➡️ [FakeUserRepo] setUserStatus() dipanggil. ID: $id, Active: $isActive")

        val existing = users[id]
        if (existing == null) {
            println("     ❌ Gagal: User tidak ditemukan")
            return Result.failure(Exception("User tidak ditemukan"))
        }

        // Update status
        users[id] = existing.copy(isActive = isActive)
        println("     ✅ Sukses: Status user '${existing.namaLengkap}' diubah jadi $isActive")
        return Result.success(Unit)
    }

    // Helper
    fun addDummyUser(user: User) {
        users[user.id] = user
    }
}