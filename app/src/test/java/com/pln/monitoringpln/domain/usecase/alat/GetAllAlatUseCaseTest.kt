package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllAlatUseCaseTest {

    private lateinit var fakeRepo: FakeAlatRepository
    private lateinit var useCase: GetAllAlatUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAlatRepository()
        useCase = GetAllAlatUseCase(fakeRepo)
    }

    @Test
    fun `invoke should return all alat from repository`() = runTest {
        // Given
        val alat1 = Alat("1", "A1", "Alat 1", 0.0, 0.0, "Normal", "Active")
        val alat2 = Alat("2", "A2", "Alat 2", 0.0, 0.0, "Normal", "Active")
        fakeRepo.addDummy(alat1)
        fakeRepo.addDummy(alat2)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
        // Note: Map doesn't guarantee order unless LinkedHashMap (which mutableMapOf usually is),
        // but to be safe we can check containment or sort.
        // For this simple fake, insertion order is likely preserved.
        val sortedResult = result.sortedBy { it.id }
        assertEquals("1", sortedResult[0].id)
        assertEquals("2", sortedResult[1].id)
    }

    @Test
    fun `invoke should return empty list if repository is empty`() = runTest {
        // Given
        fakeRepo.clear()

        // When
        val result = useCase().first()

        // Then
        assertEquals(0, result.size)
    }
}
