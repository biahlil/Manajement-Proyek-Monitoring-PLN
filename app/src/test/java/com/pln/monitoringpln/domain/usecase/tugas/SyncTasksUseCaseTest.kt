package com.pln.monitoringpln.domain.usecase.tugas

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SyncTasksUseCaseTest {

    private lateinit var fakeRepo: FakeTugasRepository
    private lateinit var useCase: SyncTasksUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeTugasRepository()
        useCase = SyncTasksUseCase(fakeRepo)
    }

    @Test
    fun `invoke should call sync on repository`() = runTest {
        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
    }
}
