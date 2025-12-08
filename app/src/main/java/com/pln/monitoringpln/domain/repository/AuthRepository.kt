package com.pln.monitoringpln.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
    fun isUserLoggedIn(): Flow<Boolean>
    suspend fun getCurrentUserEmail(): String?
    suspend fun getCurrentUserId(): String?
    suspend fun getUserRole(): Result<String>
    suspend fun createUser(email: String, password: String, fullName: String, role: String, photoUrl: String? = null): Result<Unit>
    suspend fun loadSession()
    suspend fun updatePassword(password: String): Result<Unit>
}
