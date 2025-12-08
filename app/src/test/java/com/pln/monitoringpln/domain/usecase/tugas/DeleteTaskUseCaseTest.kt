package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class DeleteTaskUseCaseTest {

    private lateinit var fakeRepo: FakeTugasRepository
    private lateinit var useCase: DeleteTaskUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeTugasRepository()
        useCase = DeleteTaskUseCase(fakeRepo)
    }

    @Test
    fun `invoke with valid id should delete task`() = runTest {
        // Given
        val task = Tugas("1", "Judul", "Deskripsi", "A1", "T1", Date(), Date(), "TODO")
        fakeRepo.addTask(task)

        // When
        val result = useCase("1")

        // Then
        assertTrue(result.isSuccess)
        val check = fakeRepo.getTaskDetail("1")
        assertTrue(check.isFailure)
    }

    @Test
    fun `invoke with invalid id should fail`() = runTest {
        // When
        val result = useCase("")

        // Then
        assertTrue(result.isFailure)
        assertEquals("ID Tugas tidak valid", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with non-existent id should fail`() = runTest {
        // When
        val result = useCase("999")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Task not found", result.exceptionOrNull()?.message)
    }
}
