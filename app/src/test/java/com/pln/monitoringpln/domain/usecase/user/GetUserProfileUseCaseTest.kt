package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import com.pln.monitoringpln.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserProfileUseCaseTest {

    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeUserRepo: FakeUserRepository
    private lateinit var useCase: GetUserProfileUseCase

    @Before
    fun setUp() {
        fakeAuthRepo = FakeAuthRepository()
        fakeUserRepo = FakeUserRepository()
        useCase = GetUserProfileUseCase(fakeAuthRepo, fakeUserRepo)
    }

    @Test
    fun `invoke should return user profile when logged in`() = runTest {
        // Given
        fakeAuthRepo.login("test@pln.co.id", "pass")
        val userId = fakeAuthRepo.getCurrentUserId()!! // "user-123"
        val user = User(userId, "test@pln.co.id", "Test User", "Teknisi", true)
        fakeUserRepo.addDummy(user)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test User", result.getOrNull()?.namaLengkap)
    }

    @Test
    fun `invoke should fail when not logged in`() = runTest {
        // Given
        fakeAuthRepo.logout()

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }
}
