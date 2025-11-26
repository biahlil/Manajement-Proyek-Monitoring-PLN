package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckUserRoleUseCaseTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var useCase: CheckUserRoleUseCase

    private val logTestStart = "\n--- 🔴 TEST START: %s ---"
    private val logAct = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logSuccess = "--- ✅ LULUS ---"

    @Before
    fun setUp() {
        authRepository = FakeAuthRepository()
        useCase = CheckUserRoleUseCase(authRepository)
    }

    @Test
    fun `should return role when user is logged in`() = runTest {
        println(logTestStart.format("Get User Role Success"))
        
        // Given
        authRepository.fakeRole = "ADMIN"
        println(logAct.format("Get role for logged in user"))

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("ADMIN", result.getOrNull())
        println(logAssert.format("Role is ADMIN"))
        println(logSuccess)
    }

    @Test
    fun `should return failure when repository fails`() = runTest {
        println(logTestStart.format("Get User Role Failure"))
        
        // Given
        authRepository.shouldFailRole = true
        println(logAct.format("Get role when repo fails"))

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        println(logAssert.format("Result is failure"))
        println(logSuccess)
    }
}
