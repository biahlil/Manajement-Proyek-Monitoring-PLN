package com.pln.monitoringpln.domain.usecase.technician

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.usecase.user.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTechniciansUseCaseTest {

    private lateinit var fakeRepo: FakeUserRepository
    private lateinit var useCase: GetTechniciansUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeUserRepository()
        useCase = GetTechniciansUseCase(fakeRepo)
    }

    @Test
    fun `invoke should return technicians`() = runTest {
        // Given
        val tech1 = User("1", "t1@pln.co.id", "Tech 1", "Teknisi", true)
        val tech2 = User("2", "t2@pln.co.id", "Tech 2", "Teknisi", true)
        fakeRepo.addDummy(tech1)
        fakeRepo.addDummy(tech2)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `refresh should call repository refresh`() = runTest {
        // When
        val result = useCase.refresh()

        // Then
        assertTrue(result.isSuccess)
    }
}
