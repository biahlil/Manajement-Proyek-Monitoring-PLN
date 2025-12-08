package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.usecase.alat.FakeAlatRepository
import com.pln.monitoringpln.domain.usecase.user.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class UpdateTaskUseCaseTest {

    private lateinit var fakeTugasRepo: FakeTugasRepository
    private lateinit var fakeAlatRepo: FakeAlatRepository
    private lateinit var fakeUserRepo: FakeUserRepository
    private lateinit var useCase: UpdateTaskUseCase

    @Before
    fun setUp() {
        fakeTugasRepo = FakeTugasRepository()
        fakeAlatRepo = FakeAlatRepository()
        fakeUserRepo = FakeUserRepository()
        useCase = UpdateTaskUseCase(fakeTugasRepo, fakeAlatRepo, fakeUserRepo)
    }

    @Test
    fun `invoke with valid data should update task`() = runTest {
        // Given
        val alat = Alat("A1", "K1", "Trafo", 0.0, 0.0, "Normal", "Active")
        val user = User("T1", "tech@pln.co.id", "Budi", "Teknisi", true)
        val task = Tugas("1", "Judul Lama", "Desk Lama", "A1", "T1", Date(), Date(), "TODO")

        fakeAlatRepo.addDummy(alat)
        fakeUserRepo.addDummy(user)
        fakeTugasRepo.addTask(task)

        // When
        val newDate = Date()
        val result = useCase("1", "Judul Baru", "Desk Baru", "A1", "T1", newDate, "IN_PROGRESS")

        // Then
        assertTrue(result.isSuccess)
        val updated = fakeTugasRepo.getTaskDetail("1").getOrNull()
        assertEquals("Judul Baru", updated?.judul)
        assertEquals("IN_PROGRESS", updated?.status)
    }

    @Test
    fun `invoke with invalid id should fail`() = runTest {
        val result = useCase("", "J", "D", "A", "T", Date(), "S")
        assertTrue(result.isFailure)
        assertEquals("ID Tugas tidak valid.", result.exceptionOrNull()?.message)
    }
}
