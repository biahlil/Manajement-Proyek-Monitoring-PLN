package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.data.model.ProfileDto
import com.pln.monitoringpln.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.*
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        supabaseClient.auth.signOut()
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return supabaseClient.auth.sessionStatus.map { status ->
            status is SessionStatus.Authenticated
        }
    }

    override suspend fun getCurrentUserEmail(): String? {
        return supabaseClient.auth.currentUserOrNull()?.email
    }

    override suspend fun getUserRole(): Result<String> {
        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("User not logged in"))

            val profile = supabaseClient.postgrest["profiles"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingle<ProfileDto>()

            Result.success(profile.role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @kotlinx.serialization.Serializable
    data class CreateUserParams(
        val email: String,
        val password: String,
        val fullName: String,
        val role: String,
    )

    override suspend fun createUser(email: String, password: String, fullName: String, role: String): Result<Unit> {
        return try {
            val params = CreateUserParams(
                email = email,
                password = password,
                fullName = fullName,
                role = role,
            )
            supabaseClient.functions.invoke("create-user", params)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
