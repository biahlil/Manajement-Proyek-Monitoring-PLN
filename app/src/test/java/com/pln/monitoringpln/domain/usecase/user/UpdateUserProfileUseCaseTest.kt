package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateUserProfileUseCaseTest {

    private lateinit var fakeRepo: FakeUserRepository
    private lateinit var useCase: UpdateUserProfileUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeUserRepository()
        useCase = UpdateUserProfileUseCase(fakeRepo)
    }

    @Test
    fun `invoke with valid data should update user`() = runTest {
        // Given
        val user = User("1", "test@pln.co.id", "Test", "Teknisi", true)
        fakeRepo.addDummy(user)

        // When
        val updatedUser = user.copy(namaLengkap = "Updated Name")
        val result = useCase(updatedUser)

        // Then
        assertTrue(result.isSuccess)
        val check = fakeRepo.getTeknisiDetail("1").getOrNull()
        assertEquals("Updated Name", check?.namaLengkap)
    }

    @Test
    fun `invoke with empty name should fail`() = runTest {
        val user = User("1", "test@pln.co.id", "", "Teknisi", true)
        val result = useCase(user)
        assertTrue(result.isFailure)
        assertEquals("Nama tidak boleh kosong", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with invalid email should fail`() = runTest {
        val user = User("1", "invalid-email", "Test", "Teknisi", true)
        val result = useCase(user)
        assertTrue(result.isFailure)
        assertEquals("Format email tidak valid", result.exceptionOrNull()?.message)
    }
}
