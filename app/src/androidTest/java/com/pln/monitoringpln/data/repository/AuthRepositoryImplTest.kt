package com.pln.monitoringpln.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pln.monitoringpln.di.SupabaseModule
import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthRepositoryImplTest {

    private lateinit var authRepository: AuthRepositoryImpl

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        val supabaseClient = SupabaseModule.provideSupabaseClient()
        authRepository = AuthRepositoryImpl(supabaseClient)
    }

    @Test
    fun loginWithInvalidCredentialsShouldFail() = runBlocking {
        println(logHeader.format("Integration: Login Invalid Credentials"))

        // Act
        val result = authRepository.login("invalid@pln.co.id", "wrongpassword")
        println(logAction.format("Attempt login with invalid creds"))

        // Assert
        assertTrue("Result should be failure", result.isFailure)
        println(logAssert.format("Login failed as expected"))
        println(logResult)
    }

    @Test
    fun loginWithValidCredentialsShouldSuccess() = runBlocking {
        println(logHeader.format("Integration: Login Valid Credentials"))

        // Act
        val result = authRepository.login(TestObjects.ADMIN_USER_EMAIL, TestObjects.ADMIN_USER_PASSWORD)
        println(logAction.format("Attempt login with valid creds"))

        // Assert
        if (result.isFailure) {
            val error = result.exceptionOrNull()
            println("❌ LOGIN FAILED: ${error?.message}")
            error?.printStackTrace()
        }
        assertTrue("Result should be success. Error: ${result.exceptionOrNull()?.message}", result.isSuccess)
        println(logAssert.format("Login success"))
        println(logResult)
    }
    
    @Test
    fun isUserLoggedInShouldReturnTrueAfterLogin() = runBlocking {
        println(logHeader.format("Integration: Check Session Status After Login"))

        // Ensure logged in
        authRepository.login(TestObjects.ADMIN_USER_EMAIL, TestObjects.ADMIN_USER_PASSWORD)

        // Act
        val isLoggedIn = authRepository.isUserLoggedIn().first()
        println(logAction.format("Check isUserLoggedIn flow"))

        // Assert
        assertTrue("User should be logged in", isLoggedIn)
        println(logAssert.format("Flow emitted value: $isLoggedIn"))
        println(logResult)
    }
}
