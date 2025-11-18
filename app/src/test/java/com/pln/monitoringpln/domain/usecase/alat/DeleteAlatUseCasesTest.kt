package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteAlatUseCasesTest {

    // Gunakan Fake yang sudah dipisah tadi
    private lateinit var fakeRepo: FakeAlatRepository

    private lateinit var requestDeleteUseCase: RequestDeleteAlatUseCase
    private lateinit var approveDeleteUseCase: ApproveDeleteAlatUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAlatRepository()
        requestDeleteUseCase = RequestDeleteAlatUseCase(fakeRepo)
        approveDeleteUseCase = ApproveDeleteAlatUseCase(fakeRepo)
    }

    // ==========================================
    // 1. TEST SUITE: Request Delete (Admin)
    // ==========================================

    @Test
    fun `admin requests delete, status should change to PENDING_DELETE`() = runTest {
        println("\n--- 🔴 TEST: Admin Request Delete (Sukses) ---")
        val alat = Alat(id = "1", kodeAlat = "K", namaAlat = "Trafo", latitude = 0.0, longitude = 0.0, kondisi = "Baik", status = "ACTIVE")
        fakeRepo.addDummy(alat)

        val result = requestDeleteUseCase("1")

        assertTrue(result.isSuccess)
        val updated = fakeRepo.database["1"]
        assertEquals("PENDING_DELETE", updated?.status)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `admin requests delete for non-existent ID, should fail`() = runTest {
        println("\n--- 🔴 TEST: Admin Request ID Tidak Ada ---")
        val result = requestDeleteUseCase("id-gaib")

        assertTrue(result.isFailure)
        assertEquals("Alat tidak ditemukan", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `admin requests delete with empty ID, should fail validation`() = runTest {
        println("\n--- 🔴 TEST: Admin Request ID Kosong ---")
        val result = requestDeleteUseCase("")

        assertTrue(result.isFailure)
        // Pastikan pesan error validasi benar (Logic ini ada di UseCase)
        assertTrue(result.exceptionOrNull()?.message?.contains("ID") == true)
        println("--- ✅ LULUS ---")
    }

    // ==========================================
    // 2. TEST SUITE: Approve Delete (Teknisi)
    // ==========================================

    @Test
    fun `technician approves delete AND id matches, should DELETE`() = runTest {
        println("\n--- 🔴 TEST: Teknisi Approve (Sukses) ---")
        // Arrange: Status PENDING_DELETE dan ID cocok
        val alat = Alat(id = "1", kodeAlat = "K", namaAlat = "T", latitude = 0.0, longitude = 0.0, kondisi = "B",
            status = "PENDING_DELETE", lastModifiedById = "teknisi-asli")
        fakeRepo.addDummy(alat)

        val result = approveDeleteUseCase(alatId = "1", technicianId = "teknisi-asli")

        assertTrue(result.isSuccess)
        assertNull("Data harusnya hilang dari DB", fakeRepo.database["1"])
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `technician approves delete BUT id mismatch, should FAIL`() = runTest {
        println("\n--- 🔴 TEST: Teknisi Salah (Gagal Otorisasi) ---")
        val alat = Alat(id = "1", kodeAlat = "K", namaAlat = "T", latitude = 0.0, longitude = 0.0, kondisi = "B",
            status = "PENDING_DELETE", lastModifiedById = "teknisi-asli")
        fakeRepo.addDummy(alat)

        val result = approveDeleteUseCase(alatId = "1", technicianId = "teknisi-palsu")

        assertTrue(result.isFailure)
        assertEquals("Anda bukan teknisi terakhir yang mengubah alat ini.", result.exceptionOrNull()?.message)
        assertNotNull("Data harusnya masih ada", fakeRepo.database["1"])
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `technician approves delete BUT status is NOT pending, should FAIL`() = runTest {
        println("\n--- 🔴 TEST: Teknisi Approve padahal Status Masih ACTIVE ---")
        // Arrange: ID cocok TAPI Status masih "ACTIVE" (Belum di-request Admin)
        val alat = Alat(id = "1", kodeAlat = "K", namaAlat = "T", latitude = 0.0, longitude = 0.0, kondisi = "B",
            status = "ACTIVE", lastModifiedById = "teknisi-asli")
        fakeRepo.addDummy(alat)

        val result = approveDeleteUseCase(alatId = "1", technicianId = "teknisi-asli")

        // Assert: Harusnya gagal karena belum ada request dari Admin
        assertTrue("Harusnya Gagal karena status bukan PENDING_DELETE", result.isFailure)
        assertEquals("Penghapusan belum diajukan oleh Admin.", result.exceptionOrNull()?.message)
        assertNotNull(fakeRepo.database["1"])
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `approve delete for non-existent ID, should fail`() = runTest {
        println("\n--- 🔴 TEST: Approve ID Tidak Ada ---")
        val result = approveDeleteUseCase(alatId = "id-hilang", technicianId = "any")

        assertTrue(result.isFailure)
        assertEquals("Alat tidak ditemukan", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }
}