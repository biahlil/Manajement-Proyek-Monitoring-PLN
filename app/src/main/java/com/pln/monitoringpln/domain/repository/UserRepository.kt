package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun addTeknisi(email: String, password: String, namaLengkap: String): Result<User>

    suspend fun getTeknisiDetail(id: String): Result<User>
    suspend fun deleteUser(id: String): Result<Unit>

    suspend fun getAllTeknisi(): Result<List<User>>
    fun observeTeknisi(): kotlinx.coroutines.flow.Flow<List<User>>
    suspend fun refreshTeknisi(): Result<Unit>

    suspend fun updateUser(user: User): Result<Unit>

    // Untuk Soft Delete / Re-activate
    suspend fun setUserStatus(id: String, isActive: Boolean): Result<Unit>

    fun getUserProfileFlow(id: String): kotlinx.coroutines.flow.Flow<User?>

    suspend fun syncProfile(): Result<Unit>

    suspend fun uploadAvatar(userId: String, byteArray: ByteArray): Result<String>
}
