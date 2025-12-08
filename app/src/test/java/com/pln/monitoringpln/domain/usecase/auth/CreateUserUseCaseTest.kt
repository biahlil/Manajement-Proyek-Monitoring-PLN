package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.data.repository.FakeAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateUserUseCaseTest {

    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var fakeAuthRepository: FakeAuthRepository

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        createUserUseCase = CreateUserUseCase(fakeAuthRepository)
    }

    @Test
    fun `invoke should return success when input is valid`() = runBlocking {
        val result = createUserUseCase("new@pln.co.id", "password123", "New User", "TEKNISI")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when email is blank`() = runBlocking {
        val result = createUserUseCase("", "password123", "New User", "TEKNISI")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "All fields must be filled")
    }

    @Test
    fun `invoke should return failure when password is too short`() = runBlocking {
        val result = createUserUseCase("new@pln.co.id", "123", "New User", "TEKNISI")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "Password must be at least 6 characters")
    }

    @Test
    fun `invoke should return failure when role is invalid`() = runBlocking {
        val result = createUserUseCase("new@pln.co.id", "password123", "New User", "INVALID_ROLE")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "Invalid role: INVALID_ROLE")
    }

    @Test
    fun `invoke should return failure when repository fails`() = runBlocking {
        fakeAuthRepository.shouldFailCreateUser = true
        val result = createUserUseCase("new@pln.co.id", "password123", "New User", "TEKNISI")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "Failed to create user")
    }
}
