package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetCurrentUserIdUseCaseTest {

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: GetCurrentUserIdUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = GetCurrentUserIdUseCase(fakeRepo)
    }

    @Test
    fun `invoke should return userId when logged in`() = runTest {
        // Given
        fakeRepo.login("test@pln.co.id", "password")

        // When
        val result = useCase()

        // Then
        assertEquals("user-123", result)
    }

    @Test
    fun `invoke should return null when not logged in`() = runTest {
        // Given
        fakeRepo.logout()

        // When
        val result = useCase()

        // Then
        assertNull(result)
    }
}
