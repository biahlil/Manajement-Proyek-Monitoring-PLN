package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import com.pln.monitoringpln.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveUserProfileUseCaseTest {

    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeUserRepo: FakeUserRepository
    private lateinit var useCase: ObserveUserProfileUseCase

    @Before
    fun setUp() {
        fakeAuthRepo = FakeAuthRepository()
        fakeUserRepo = FakeUserRepository()
        useCase = ObserveUserProfileUseCase(fakeAuthRepo, fakeUserRepo)
    }

    @Test
    fun `invoke should emit user profile when logged in`() = runTest {
        // Given
        fakeAuthRepo.login("test@pln.co.id", "pass")
        val userId = fakeAuthRepo.getCurrentUserId()!!
        val user = User(userId, "test@pln.co.id", "Test User", "Teknisi", true)
        fakeUserRepo.addDummy(user)

        // When
        val result = useCase().first()

        // Then
        assertEquals("Test User", result?.namaLengkap)
    }
}
