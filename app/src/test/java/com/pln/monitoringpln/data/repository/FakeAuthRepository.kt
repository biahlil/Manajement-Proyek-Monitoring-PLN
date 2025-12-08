package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(false)
    private var currentUserEmail: String? = null

    // Configurable behavior for testing
    var shouldFailLogin = false
    var failureMessage = "Login failed"

    override suspend fun login(email: String, password: String): Result<Unit> {
        if (shouldFailLogin) {
            return Result.failure(Exception(failureMessage))
        }

        // Simple mock logic: accept any login if not set to fail
        if (password == "wrong") {
            return Result.failure(Exception("Invalid credentials"))
        }

        _isLoggedIn.value = true
        currentUserEmail = email
        return Result.success(Unit)
    }

    override suspend fun logout() {
        _isLoggedIn.value = false
        currentUserEmail = null
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return _isLoggedIn.asStateFlow()
    }

    override suspend fun getCurrentUserEmail(): String? {
        return currentUserEmail
    }

    // Role testing helpers
    var fakeRole = "TEKNISI"
    var shouldFailRole = false

    override suspend fun getUserRole(): Result<String> {
        if (shouldFailRole) {
            return Result.failure(Exception("Failed to get role"))
        }
        return Result.success(fakeRole)
    }

    var shouldFailCreateUser = false

    override suspend fun getCurrentUserId(): String? {
        return if (_isLoggedIn.value) "user-123" else null
    }

    override suspend fun createUser(email: String, password: String, fullName: String, role: String, photoUrl: String?): Result<Unit> {
        if (shouldFailCreateUser) {
            return Result.failure(Exception("Failed to create user"))
        }
        return Result.success(Unit)
    }

    override suspend fun loadSession() {
        // No-op for fake
    }

    var shouldFailUpdatePassword = false

    override suspend fun updatePassword(password: String): Result<Unit> {
        if (shouldFailUpdatePassword) {
            return Result.failure(Exception("Failed to update password"))
        }
        return Result.success(Unit)
    }
}
