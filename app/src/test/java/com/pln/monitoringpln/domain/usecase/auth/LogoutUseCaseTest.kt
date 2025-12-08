package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LogoutUseCaseTest {

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: LogoutUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = LogoutUseCase(fakeRepo)
    }

    @Test
    fun `invoke should call logout on repository`() = runTest {
        // Given
        fakeRepo.login("test@pln.co.id", "password")
        assertTrue(fakeRepo.isUserLoggedIn().first())

        // When
        useCase()

        // Then
        assertFalse(fakeRepo.isUserLoggedIn().first())
    }
}
