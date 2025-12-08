package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdatePasswordUseCaseTest {

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: UpdatePasswordUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = UpdatePasswordUseCase(fakeRepo)
    }

    @Test
    fun `invoke with valid password should succeed`() = runTest {
        // When
        val result = useCase("newPassword123", "newPassword123")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke with empty password should fail`() = runTest {
        // When
        val result = useCase("", "")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password tidak boleh kosong", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with short password should fail`() = runTest {
        // When
        val result = useCase("123", "123")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password minimal 6 karakter", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with mismatching passwords should fail`() = runTest {
        // When
        val result = useCase("password123", "password456")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Password tidak cocok", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke when repository fails should return failure`() = runTest {
        // Given
        fakeRepo.shouldFailUpdatePassword = true

        // When
        val result = useCase("validPass", "validPass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Failed to update password", result.exceptionOrNull()?.message)
    }
}
