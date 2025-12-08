package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteUserUseCaseTest {

    private lateinit var fakeRepo: FakeUserRepository
    private lateinit var useCase: DeleteUserUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeUserRepository()
        useCase = DeleteUserUseCase(fakeRepo)
    }

    @Test
    fun `invoke should delete user`() = runTest {
        // Given
        val user = User("1", "test@pln.co.id", "Test", "Teknisi", true)
        fakeRepo.addDummy(user)

        // When
        val result = useCase("1")

        // Then
        assertTrue(result.isSuccess)
        val check = fakeRepo.getTeknisiDetail("1")
        assertTrue(check.isFailure)
    }
}
