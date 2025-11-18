package com.pln.monitoringpln.domain.repository

import com.pln.monitoringpln.domain.model.User

interface UserRepository {
    // Kita butuh 'suspend' karena ini operasi jaringan/async
    suspend fun login(email: String, password: String): Result<User>
}