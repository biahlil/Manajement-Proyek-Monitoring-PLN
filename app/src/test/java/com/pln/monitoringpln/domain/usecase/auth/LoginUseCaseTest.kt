package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var useCase: LoginUseCase

    private val logTestStart = "\n--- 🔴 TEST START: %s ---"
    private val logAct = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logSuccess = "--- ✅ LULUS ---"

    @Before
    fun setUp() {
        authRepository = FakeAuthRepository()
        useCase = LoginUseCase(authRepository)
    }

    @Test
    fun `login successful, should return Success`() = runTest {
        println(logTestStart.format("Login Successful"))
        println(logAct.format("Login user 'valid@pln.co.id'"))

        // Given
        // FakeRepo defaults to success

        // When
        val result = useCase("valid@pln.co.id", "password123")

        // Then
        println(logAssert.format("Sukses"))
        assertTrue(result.isSuccess)
        println(logSuccess)
    }

    @Test
    fun `login failure from repository, should return Failure`() = runTest {
        println(logTestStart.format("Login Failure (Repo)"))
        println(logAct.format("Login with wrong password"))

        // Given
        authRepository.shouldFailLogin = true
        authRepository.failureMessage = "Invalid login credentials"

        // When
        val result = useCase("valid@pln.co.id", "wrong")

        // Then
        println(logAssert.format("Gagal sesuai repo"))
        assertTrue(result.isFailure)
        assertEquals("Invalid login credentials", result.exceptionOrNull()?.message)
        println(logSuccess)
    }

    @Test
    fun `login with empty email, should fail validation`() = runTest {
        println(logTestStart.format("Empty Email"))
        val result = useCase("", "password123")
        assertTrue(result.isFailure)
        assertEquals("Email tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }

    @Test
    fun `login with invalid email format, should fail validation`() = runTest {
        println(logTestStart.format("Invalid Email Format"))
        val result = useCase("bukan-email", "password123")
        assertTrue(result.isFailure)
        assertEquals("Format email tidak valid.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }
}
