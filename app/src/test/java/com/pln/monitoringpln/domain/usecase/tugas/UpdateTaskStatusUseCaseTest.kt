package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpdateTaskStatusUseCaseTest {

    private val fakeRepo = FakeTugasRepository()
    private val useCase = UpdateTaskStatusUseCase(fakeRepo)

    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        fakeRepo.clear()
        // Setup: Hanya masukkan tugas 'To Do'
        fakeRepo.addDummyTasks(listOf(TestObjects.TUGAS_TODO))
    }

    // --- HAPPY PATH ---

    @Test
    fun `update status to In Progress, should success`() = runTest {
        println(logHeader.format("Update Status Valid (In Progress)"))
        val targetTask = TestObjects.TUGAS_TODO

        println(logAction.format("Ubah status tugas ke 'In Progress'"))
        val result = useCase(targetTask.id, "In Progress")

        println(logAssert.format("Sukses"))
        assertTrue(result.isSuccess)
        val updatedTask = fakeRepo.getTasksByTeknisi(targetTask.idTeknisi).getOrNull()?.find { it.id == targetTask.id }
        assertEquals("In Progress", updatedTask?.status)
        println(logResult)
    }

    @Test
    fun `update status to Done, should success`() = runTest {
        println(logHeader.format("Update Status Valid (Done)"))
        val targetTask = TestObjects.TUGAS_TODO

        println(logAction.format("Ubah status tugas ke 'Done'"))
        val result = useCase(targetTask.id, "Done")

        println(logAssert.format("Sukses"))
        assertTrue(result.isSuccess)
        val updatedTask = fakeRepo.getTasksByTeknisi(targetTask.idTeknisi).getOrNull()?.find { it.id == targetTask.id }
        assertEquals("Done", updatedTask?.status)
        println(logResult)
    }

    @Test
    fun `update status to same status (To Do), should success (no-op)`() = runTest {
        println(logHeader.format("Update Same Status"))
        val targetTask = TestObjects.TUGAS_TODO // Awalnya "To Do"

        println(logAction.format("Ubah status tugas ke 'To Do' (Sama dengan sebelumnya)"))
        val result = useCase(targetTask.id, "To Do")

        println(logAssert.format("Sukses"))
        assertTrue(result.isSuccess)
        val updatedTask = fakeRepo.getTasksByTeknisi(targetTask.idTeknisi).getOrNull()?.find { it.id == targetTask.id }
        assertEquals("To Do", updatedTask?.status)
        println(logResult)
    }

    // --- VALIDATION CASES ---

    @Test
    fun `update status with Invalid String, should fail validation`() = runTest {
        println(logHeader.format("Update Status Invalid (Random Word)"))
        val result = useCase(TestObjects.TUGAS_TODO.id, "Pendingan")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Status tidak valid") == true)
        println(logResult)
    }

    @Test
    fun `update status with Lowercase (case sensitive), should fail validation`() = runTest {
        println(logHeader.format("Update Status Invalid Case (lowercase)"))
        // Sistem kita strict: "Done" != "done"
        val result = useCase(TestObjects.TUGAS_TODO.id, "done")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Status tidak valid") == true)
        println(logResult)
    }

    @Test
    fun `update with Empty Status, should fail validation`() = runTest {
        println(logHeader.format("Update Empty Status"))
        val result = useCase(TestObjects.TUGAS_TODO.id, "")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Status tidak valid") == true)
        println(logResult)
    }

    @Test
    fun `update with Empty Task ID, should fail validation`() = runTest {
        println(logHeader.format("Update Empty Task ID"))
        val result = useCase("", "Done")

        assertTrue(result.isFailure)
        assertEquals("ID Tugas tidak valid.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `update with Whitespace Task ID, should fail validation`() = runTest {
        println(logHeader.format("Update Whitespace Task ID"))
        val result = useCase("   ", "Done")

        assertTrue(result.isFailure)
        assertEquals("ID Tugas tidak valid.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    // --- DATA INTEGRITY ---

    @Test
    fun `update non-existent task, should fail from repo`() = runTest {
        println(logHeader.format("Update ID Hantu"))
        val result = useCase("task-gaib", "Done")

        assertTrue(result.isFailure)
        assertEquals("Tugas tidak ditemukan", result.exceptionOrNull()?.message)
        println(logResult)
    }
}
