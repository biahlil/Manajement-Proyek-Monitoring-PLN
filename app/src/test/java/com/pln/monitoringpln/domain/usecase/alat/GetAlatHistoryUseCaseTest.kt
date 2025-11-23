package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.usecase.tugas.FakeTugasRepository
import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetAlatHistoryUseCaseTest {

    private lateinit var fakeAlatRepo: FakeAlatRepository
    private lateinit var fakeTugasRepo: FakeTugasRepository
    private lateinit var useCase: GetAlatHistoryUseCase

    // Log Format
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        fakeAlatRepo = FakeAlatRepository()
        fakeTugasRepo = FakeTugasRepository()
        useCase = GetAlatHistoryUseCase(fakeAlatRepo, fakeTugasRepo)

        // Setup Data
        fakeAlatRepo.clear()
        fakeTugasRepo.clear()

        // 1. Insert Alat Valid (alat-1)
        fakeAlatRepo.addDummy(TestObjects.ALAT_VALID)

        // 2. Insert Tugas (2 tugas milik alat-1, 0 milik alat lain)
        fakeTugasRepo.addDummyTasks(
            listOf(
                TestObjects.TUGAS_TODO, // alat-1
                TestObjects.TUGAS_IN_PROGRESS, // alat-1
            ),
        )
    }

    // ==========================================
    // 1. HAPPY PATH
    // ==========================================

    @Test
    fun `get history for existing alat, should return Alat data AND Task list`() = runTest {
        println(logHeader.format("Happy Path: Alat & History Found"))

        println(logAction.format("Ambil history untuk '${TestObjects.ALAT_VALID.namaAlat}'"))
        val result = useCase(TestObjects.ALAT_VALID.id)

        println(logAssert.format("Sukses"))
        assertTrue(result.isSuccess)

        val history = result.getOrNull()!!

        // Cek Alat
        println(logAssert.format("Nama Alat harus '${TestObjects.ALAT_VALID.namaAlat}'"))
        assertEquals(TestObjects.ALAT_VALID.namaAlat, history.alat.namaAlat)

        // Cek Tugas
        println(logAssert.format("Jumlah riwayat tugas harus 2"))
        assertEquals(2, history.riwayatTugas.size)

        println(logAssert.format("Semua tugas harus milik ID '${TestObjects.ALAT_VALID.id}'"))
        assertTrue(history.riwayatTugas.all { it.idAlat == TestObjects.ALAT_VALID.id })

        println(logResult)
    }

    @Test
    fun `get history for alat with NO tasks, should return Alat and Empty List`() = runTest {
        println(logHeader.format("Edge Case: Alat No History"))

        // Arrange: Buat alat baru tanpa tugas
        val newAlat = TestObjects.ALAT_VALID.copy(id = "alat-baru", namaAlat = "Alat Baru")
        fakeAlatRepo.addDummy(newAlat)

        println(logAction.format("Ambil history untuk 'alat-baru'"))
        val result = useCase("alat-baru")

        println(logAssert.format("Sukses (walau tugas kosong)"))
        assertTrue(result.isSuccess)

        val history = result.getOrNull()!!
        println(logAssert.format("ID Alat harus 'alat-baru'"))
        assertEquals("alat-baru", history.alat.id)

        println(logAssert.format("List tugas harus kosong (bukan error)"))
        assertTrue(history.riwayatTugas.isEmpty())

        println(logResult)
    }
    // ==========================================
    // 2. NEGATIVE CASES (Alat Not Found / Invalid)
    // ==========================================

    @Test
    fun `get history for non-existent alat, should fail`() = runTest {
        println(logHeader.format("Negative Case: Alat Not Found"))

        println(logAction.format("Cari ID 'id-gaib'"))
        val result = useCase("id-gaib")

        println(logAssert.format("Gagal: Pesan 'Alat tidak ditemukan'"))
        assertTrue(result.isFailure)
        assertEquals("Alat tidak ditemukan", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `get history with empty ID, should fail validation`() = runTest {
        println(logHeader.format("Validation: Empty ID"))

        println(logAction.format("Input ID kosong"))
        val result = useCase("")

        println(logAssert.format("Gagal Validasi"))
        assertTrue(result.isFailure)
        assertEquals("ID Alat tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logResult)
    }
}
