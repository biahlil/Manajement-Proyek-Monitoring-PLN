package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun addTeknisi(email: String, password: String, namaLengkap: String): Result<User>

    suspend fun getTeknisiDetail(id: String): Result<User>

    // Untuk Soft Delete / Re-activate
    suspend fun setUserStatus(id: String, isActive: Boolean): Result<Unit>
}
