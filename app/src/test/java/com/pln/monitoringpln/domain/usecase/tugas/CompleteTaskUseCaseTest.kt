package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.usecase.alat.FakeAlatRepository
import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CompleteTaskUseCaseTest {

    private lateinit var fakeTugasRepo: FakeTugasRepository
    private lateinit var fakeAlatRepo: FakeAlatRepository
    private lateinit var useCase: CompleteTaskUseCase

    // Log Helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        fakeTugasRepo = FakeTugasRepository()
        fakeAlatRepo = FakeAlatRepository()
        useCase = CompleteTaskUseCase(fakeTugasRepo, fakeAlatRepo)

        // Seed Data Standar
        fakeAlatRepo.addDummy(TestObjects.ALAT_VALID) // ID: alat-1
        fakeTugasRepo.addDummyTasks(listOf(TestObjects.TUGAS_IN_PROGRESS)) // ID: task-2
    }

    // ==========================================
    // 1. HAPPY PATH (Skenario Ideal)
    // ==========================================

    @Test
    fun `complete task (Normal Flow), should update status AND alat condition`() = runTest {
        println(logHeader.format("Complete Task Success Flow"))

        val photoBytes = ByteArray(1024) // 1KB Foto
        val newCondition = "Rusak Ringan"

        println(logAction.format("Selesaikan tugas dengan data valid"))
        val result = useCase(TestObjects.TUGAS_IN_PROGRESS.id, photoBytes, newCondition)

        println(logAssert.format("Sukses"))
        assertTrue("UseCase harus sukses", result.isSuccess)

        // Verifikasi Status Tugas -> Done
        val updatedTask = fakeTugasRepo.getTaskDetail(TestObjects.TUGAS_IN_PROGRESS.id).getOrNull()
        assertEquals("Done", updatedTask?.status)

        // Verifikasi Kondisi Alat -> Rusak Ringan
        val updatedAlat = fakeAlatRepo.getAlatDetail(TestObjects.ALAT_VALID.id).getOrNull()
        assertEquals("Rusak Ringan", updatedAlat?.kondisi)

        println(logResult)
    }

    // ==========================================
    // 2. INPUT VALIDATION (Strict Rules)
    // ==========================================

    @Test
    fun `input empty Task ID, should fail fast`() = runTest {
        println(logHeader.format("Validation: Empty Task ID"))
        // Foto dan kondisi valid, tapi ID kosong
        val result = useCase("", ByteArray(10), "Baik")

        assertTrue(result.isFailure)
        assertEquals("ID Tugas tidak valid.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `input whitespace Task ID, should fail fast`() = runTest {
        println(logHeader.format("Validation: Whitespace Task ID"))
        val result = useCase("   ", ByteArray(10), "Baik")

        assertTrue(result.isFailure)
        assertEquals("ID Tugas tidak valid.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `input empty Photo (0 bytes), should fail validation`() = runTest {
        println(logHeader.format("Validation: Empty Photo"))
        // ID valid, Kondisi valid, Foto 0 bytes
        val result = useCase(TestObjects.TUGAS_IN_PROGRESS.id, ByteArray(0), "Baik")

        assertTrue(result.isFailure)
        assertEquals("Foto bukti wajib diunggah.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `input empty Condition string, should fail validation`() = runTest {
        println(logHeader.format("Validation: Empty Condition"))
        val result = useCase(TestObjects.TUGAS_IN_PROGRESS.id, ByteArray(10), "")

        assertTrue(result.isFailure)
        assertEquals("Kondisi alat wajib diisi.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `input whitespace Condition string, should fail validation`() = runTest {
        println(logHeader.format("Validation: Whitespace Condition"))
        val result = useCase(TestObjects.TUGAS_IN_PROGRESS.id, ByteArray(10), "   ")

        assertTrue(result.isFailure)
        assertEquals("Kondisi alat wajib diisi.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    // ==========================================
    // 3. DATA INTEGRITY & EDGE CASES (The "Break" Scenarios)
    // ==========================================

    @Test
    fun `task not found in DB, should return specific error`() = runTest {
        println(logHeader.format("Edge Case: Task ID Not Found"))

        val result = useCase("id-tugas-gaib", ByteArray(10), "Baik")

        assertTrue(result.isFailure)
        assertEquals("Task not found", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `task found BUT alat referenced is missing (Orphaned Task), should fail`() = runTest {
        println(logHeader.format("Edge Case: Orphaned Task (Alat Missing)"))

        // Hapus data alat, tapi biarkan tugasnya
        fakeAlatRepo.clear()

        val result = useCase(TestObjects.TUGAS_IN_PROGRESS.id, ByteArray(10), "Baik")

        // Harus gagal saat mencoba update kondisi alat
        assertTrue(result.isFailure)
        assertEquals("Alat tidak ditemukan", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `condition with special characters, should allowed (flexible input)`() = runTest {
        println(logHeader.format("Edge Case: Special Char Condition"))

        val weirdCondition = "Rusak @!#$%^&*() Parah"
        val result = useCase(TestObjects.TUGAS_IN_PROGRESS.id, ByteArray(10), weirdCondition)

        // Asumsi: Sistem membolehkan deskripsi bebas
        assertTrue(result.isSuccess)
        val updatedAlat = fakeAlatRepo.getAlatDetail(TestObjects.ALAT_VALID.id).getOrNull()
        assertEquals(weirdCondition, updatedAlat?.kondisi)

        println(logResult)
    }

    // Test untuk memastikan urutan eksekusi:
    // Jika upload gagal, status tugas & kondisi alat JANGAN berubah (Transactional-like behavior)
    // *Catatan: Karena ini simulasi tanpa Transaction DB asli, kita hanya memastikan flow berhenti saat error*
    // Test Urutan akan dilakukan di data layer?
}
