package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetMyTasksUseCaseTest {

    private val fakeRepo = FakeTugasRepository()
    private val useCase = GetMyTasksUseCase(fakeRepo)

    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        fakeRepo.clear()
        // Seeding data: Ada tugas untuk TEKNISI_VALID dan teknisi lain
        fakeRepo.addDummyTasks(listOf(
            TestObjects.TUGAS_TODO,         // Milik TEKNISI_VALID
            TestObjects.TUGAS_IN_PROGRESS,  // Milik TEKNISI_VALID
            TestObjects.TUGAS_OTHER_TECH    // Milik 'tech-2'
        ))
    }

    // --- HAPPY PATH ---

    @Test
    fun `get tasks for TEKNISI_VALID, should return only his tasks`() = runTest {
        println(logHeader.format("Get Tasks for Specific Technician"))

        println(logAction.format("Mengambil tugas untuk '${TestObjects.TEKNISI_VALID.namaLengkap}'"))
        val result = useCase(TestObjects.TEKNISI_VALID.id)

        println(logAssert.format("Sukses dan jumlah tugas harus 2"))
        assertTrue(result.isSuccess)
        val list = result.getOrNull() ?: emptyList()

        // Verifikasi Jumlah
        assertEquals(2, list.size)

        // Verifikasi Kepemilikan (Semua tugas harus milik user ini)
        assertTrue(list.all { it.idTeknisi == TestObjects.TEKNISI_VALID.id })

        // Verifikasi Konten Spesifik
        assertTrue(list.any { it.deskripsi == TestObjects.TUGAS_TODO.deskripsi })
        assertTrue(list.any { it.deskripsi == TestObjects.TUGAS_IN_PROGRESS.deskripsi })

        // Pastikan tugas teknisi lain TIDAK terbawa
        assertFalse(list.any { it.id == TestObjects.TUGAS_OTHER_TECH.id })

        println(logResult)
    }

    @Test
    fun `get tasks for unknown technician, should return empty list`() = runTest {
        println(logHeader.format("Get Tasks for Unknown Technician"))
        val result = useCase("tech-hantu")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        println(logResult)
    }

    // --- VALIDATION CASES ---

    @Test
    fun `get tasks with empty ID, should fail validation`() = runTest {
        println(logHeader.format("Get Tasks with Empty ID"))
        val result = useCase("")

        assertTrue(result.isFailure)
        assertEquals("ID Teknisi tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `get tasks with whitespace ID, should fail validation`() = runTest {
        println(logHeader.format("Get Tasks with Whitespace ID"))
        val result = useCase("   ") // Spasi saja

        assertTrue(result.isFailure)
        assertEquals("ID Teknisi tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logResult)
    }
}