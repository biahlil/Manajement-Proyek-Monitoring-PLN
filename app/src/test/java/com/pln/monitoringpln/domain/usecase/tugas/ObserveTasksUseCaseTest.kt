package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class ObserveTasksUseCaseTest {

    private lateinit var fakeRepo: FakeTugasRepository
    private lateinit var useCase: ObserveTasksUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeTugasRepository()
        useCase = ObserveTasksUseCase(fakeRepo)
    }

    @Test
    fun `invoke without idTeknisi should return all tasks`() = runTest {
        // Given
        val task1 = Tugas("1", "J1", "D1", "A1", "T1", Date(), Date(), "TODO")
        val task2 = Tugas("2", "J2", "D2", "A1", "T2", Date(), Date(), "TODO")
        fakeRepo.addTask(task1)
        fakeRepo.addTask(task2)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `invoke with idTeknisi should return filtered tasks`() = runTest {
        // Given
        val task1 = Tugas("1", "J1", "D1", "A1", "T1", Date(), Date(), "TODO")
        val task2 = Tugas("2", "J2", "D2", "A1", "T2", Date(), Date(), "TODO")
        fakeRepo.addTask(task1)
        fakeRepo.addTask(task2)

        // When
        val result = useCase("T1").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }
}
