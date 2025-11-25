package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.usecase.tugas.FakeTugasRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ArchiveAlatUseCaseTest {

    private lateinit var alatRepository: FakeAlatRepository
    private lateinit var tugasRepository: FakeTugasRepository
    private lateinit var archiveAlatUseCase: ArchiveAlatUseCase

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        alatRepository = FakeAlatRepository()
        tugasRepository = FakeTugasRepository()
        archiveAlatUseCase = ArchiveAlatUseCase(alatRepository, tugasRepository)
    }

    @Test
    fun `should archive alat successfully when no active tasks`() = runBlocking {
        println(logHeader.format("Archive Alat Success"))

        // Given
        val alat = com.pln.monitoringpln.utils.TestObjects.ALAT_VALID
        alatRepository.addDummy(alat)
        println(logAction.format("Added dummy alat: ${alat.namaAlat}"))

        // When
        val result = archiveAlatUseCase(alat.id)

        // Then
        assertTrue(result.isSuccess)

        // Verify in repo
        val archivedAlat = alatRepository.database[alat.id]
        assertNotNull(archivedAlat)
        assertTrue(archivedAlat!!.isArchived)
        assertEquals("ARCHIVED", archivedAlat.status)
        println(logAssert.format("Alat isArchived = true"))
        println(logResult)
    }

    @Test
    fun `should fail to archive alat when active tasks exist`() = runBlocking {
        println(logHeader.format("Archive Alat Fail - Active Tasks"))

        // Given
        val alat = com.pln.monitoringpln.utils.TestObjects.ALAT_VALID
        alatRepository.addDummy(alat)

        val tugas = com.pln.monitoringpln.utils.TestObjects.TUGAS_IN_PROGRESS.copy(idAlat = alat.id)
        tugasRepository.addDummyTasks(listOf(tugas))
        println(logAction.format("Added active task for ${alat.namaAlat}"))

        // When
        val result = archiveAlatUseCase(alat.id)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cannot archive alat with active tasks", result.exceptionOrNull()?.message)
        println(logAssert.format("Result is failure with correct message"))
        println(logResult)
    }

    @Test
    fun `should fail to archive non-existent alat`() = runBlocking {
        println(logHeader.format("Archive Alat Fail - Not Found"))

        // When
        val result = archiveAlatUseCase("unknown-id")

        // Then
        assertTrue(result.isFailure)
        println(logAssert.format("Result is failure"))
        println(logResult)
    }
}
