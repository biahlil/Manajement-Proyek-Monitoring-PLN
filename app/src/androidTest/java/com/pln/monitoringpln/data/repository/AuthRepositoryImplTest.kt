package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.BuildConfig.SUPABASE_KEY
import com.pln.monitoringpln.BuildConfig.SUPABASE_URL
import com.pln.monitoringpln.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.functions.Functions
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var authRepository: AuthRepository

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        // Initialize real Supabase Client for Integration Test
        supabaseClient = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Functions)
        }

        authRepository = AuthRepositoryImpl(supabaseClient)
    }

    @Test
    fun login_with_valid_credentials_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Login Success"))
        
        // Given
        val email = "boss@pln.co.id" // Valid user from Supabase
        val password = "password123"

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isSuccess)
        println(logAssert.format("Login successful"))
        
        println(logResult)
    }

    @Test
    fun login_with_invalid_credentials_should_fail() = runBlocking {
        println(logHeader.format("Integration: Login Failure"))
        
        // Given
        val email = "invalid@pln.co.id"
        val password = "wrongpassword"

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isFailure)
        println(logAssert.format("Login failed as expected: ${result.exceptionOrNull()?.message}"))
        
        println(logResult)
    }

    @Test
    fun get_user_role_should_return_valid_role() = runBlocking {
        println(logHeader.format("Integration: Get User Role"))
        
        // Given: Login first
        val email = "boss@pln.co.id"
        val password = "password123"
        authRepository.login(email, password)

        // When
        val result = authRepository.getUserRole()

        // Then
        if (result.isFailure) {
             println("Error getting role: ${result.exceptionOrNull()?.message}")
        }
        assertTrue(result.isSuccess)
        val role = result.getOrNull()
        println(logAssert.format("Role retrieved: $role"))
        assertTrue("Role must be ADMIN or TEKNISI, but was $role", role == "ADMIN" || role == "TEKNISI")
        
        println(logResult)
    }

    @Test
    fun create_user_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Create User"))

        // Given: Login as Admin
        val adminEmail = "boss@pln.co.id"
        val adminPassword = "password123"
        authRepository.login(adminEmail, adminPassword)

        // When: Create a new user
        val timestamp = System.currentTimeMillis()
        val newEmail = "test.user.$timestamp@pln.co.id"
        val newPassword = "password123"
        val fullName = "Test User $timestamp"
        val role = "TEKNISI"

        println("  [Act] Creating user: $newEmail...")
        val result = authRepository.createUser(newEmail, newPassword, fullName, role)

        // Then
        if (result.isFailure) {
            println("Error creating user: ${result.exceptionOrNull()?.message}")
        }
        assertTrue(result.isSuccess)
        println(logAssert.format("User created successfully"))

        println(logResult)
    }
}
