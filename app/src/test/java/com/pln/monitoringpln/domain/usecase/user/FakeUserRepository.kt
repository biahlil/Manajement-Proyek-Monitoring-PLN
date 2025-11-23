package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository

class FakeUserRepository : UserRepository {

    val users = mutableMapOf<String, User>()

    override suspend fun login(email: String, password: String): Result<User> {
        println("  ➡️ [FakeUserRepo] login() dipanggil. Email: $email")
        val user = users.values.find { it.email == email }

        if (user == null) return Result.failure(Exception("Kredensial salah"))
        if (password == "password123") return Result.success(user)
        return Result.failure(Exception("Kredensial salah"))
    }

    override suspend fun addTeknisi(email: String, password: String, namaLengkap: String): Result<User> {
        println("  ➡️ [FakeUserRepo] addTeknisi() dipanggil. Email: $email")

        if (users.values.any { it.email == email }) {
            println("     ❌ Gagal: Email sudah terdaftar")
            return Result.failure(Exception("Email sudah terdaftar."))
        }

        val newUser = User(
            id = "gen-${System.currentTimeMillis()}",
            email = email,
            namaLengkap = namaLengkap,
            role = "Teknisi",
            isActive = true,
        )
        users[newUser.id] = newUser
        println("     ✅ Sukses: User baru dibuat (ID: ${newUser.id})")
        return Result.success(newUser)
    }

    override suspend fun setUserStatus(id: String, isActive: Boolean): Result<Unit> {
        println("  ➡️ [FakeUserRepo] setUserStatus() dipanggil. ID: $id -> Active: $isActive")
        val user = users[id] ?: return Result.failure(Exception("User tidak ditemukan"))

        users[id] = user.copy(isActive = isActive)
        println("     ✅ Sukses Update Status")
        return Result.success(Unit)
    }

    override suspend fun getTeknisiDetail(id: String): Result<User> {
        val user = users[id]
        return if (user != null) Result.success(user) else Result.failure(Exception("User tidak ditemukan"))
    }

    fun addDummyUser(user: User) { users[user.id] = user }
    fun clear() { users.clear() }
}
