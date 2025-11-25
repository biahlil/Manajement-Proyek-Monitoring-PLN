package com.pln.monitoringpln.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUp() {
        authRepository = FakeAuthRepository()
    }

    @Test
    fun `login success should return Result Success`() = runTest {
        // Given
        // Default fake behavior is success for correct password

        // When
        val result = authRepository.login("test@pln.co.id", "password")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("test@pln.co.id", authRepository.getCurrentUserEmail())
    }

    @Test
    fun `login failure should return Result Failure`() = runTest {
        // Given
        authRepository.shouldFailLogin = true
        authRepository.failureMessage = "Invalid credentials"

        // When
        val result = authRepository.login("wrong@pln.co.id", "wrong")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `isUserLoggedIn should return flow of boolean`() = runTest {
        // Given
        authRepository.login("test@pln.co.id", "password")

        // When
        val isLoggedIn = authRepository.isUserLoggedIn().first()

        // Then
        assertTrue(isLoggedIn)
    }
}
