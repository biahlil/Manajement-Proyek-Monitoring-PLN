package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.usecase.alat.FakeAlatRepository
import com.pln.monitoringpln.domain.usecase.user.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class GetTaskDetailUseCaseTest {

    private lateinit var fakeTugasRepo: FakeTugasRepository
    private lateinit var fakeAlatRepo: FakeAlatRepository
    private lateinit var fakeUserRepo: FakeUserRepository
    private lateinit var useCase: GetTaskDetailUseCase

    @Before
    fun setUp() {
        fakeTugasRepo = FakeTugasRepository()
        fakeAlatRepo = FakeAlatRepository()
        fakeUserRepo = FakeUserRepository()
        useCase = GetTaskDetailUseCase(fakeTugasRepo, fakeAlatRepo, fakeUserRepo)
    }

    @Test
    fun `invoke should return task detail with equipment and technician`() = runTest {
        // Given
        val alat = Alat("A1", "K1", "Trafo", 0.0, 0.0, "Normal", "Active")
        val user = User("T1", "tech@pln.co.id", "Budi", "Teknisi", true)
        val task = Tugas("1", "Judul", "Deskripsi", "A1", "T1", Date(), Date(), "TODO")

        fakeAlatRepo.addDummy(alat)
        fakeUserRepo.addDummy(user)
        fakeTugasRepo.addTask(task)

        // When
        val result = useCase("1")

        // Then
        assertTrue(result.isSuccess)
        val detail = result.getOrNull()
        assertNotNull(detail)
        assertEquals("1", detail?.task?.id)
        assertEquals("A1", detail?.equipment?.id)
        assertEquals("T1", detail?.technician?.id)
    }

    @Test
    fun `invoke should return failure if task not found`() = runTest {
        // When
        val result = useCase("999")

        // Then
        assertTrue(result.isFailure)
    }
}
