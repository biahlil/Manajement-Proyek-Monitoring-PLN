package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateAlatInfoUseCaseTest {
    private lateinit var fakeRepo: FakeAlatRepository
    private lateinit var useCase: UpdateAlatInfoUseCase

    @Before fun setUp() {
        fakeRepo = FakeAlatRepository()
        useCase = UpdateAlatInfoUseCase(fakeRepo)
    }

    @Test
    fun `update info should NOT change existing condition`() = runTest {
        println("\n--- 🔴 TEST: `update info should NOT change condition` ---")

        // Arrange
        fakeRepo.addDummy(Alat("id-1", "OLD", "Old Name", 0.0, 0.0, "Rusak Parah"))

        println("  [Act] Admin update nama jadi 'New Name'...")
        val result = useCase("id-1", "New Name", "NEW", -1.0, 1.0)

        println("  [Assert] Cek apakah update sukses dan kondisi tetap 'Rusak Parah'...")
        assertTrue(result.isSuccess)
        val updated = fakeRepo.database["id-1"]
        assertEquals("New Name", updated?.namaAlat)
        assertEquals("Rusak Parah", updated?.kondisi)

        println("--- ✅ LULUS ---")
    }

    @Test
    fun `update non-existent alat, should return failure`() = runTest {
        println("\n--- 🔴 TEST: `update non-existent alat` ---")

        val result = useCase("id-hilang", "Name", "Code", 0.0, 0.0)

        assertTrue(result.isFailure)
        assertEquals("Alat tidak ditemukan", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `update with empty name, should fail validation`() = runTest {
        println("\n--- 🔴 TEST: `update with empty name` ---")

        val result = useCase("id-1", "", "Code", 0.0, 0.0)

        assertTrue(result.isFailure)
        assertEquals("Nama alat tidak boleh kosong.", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `update with invalid coordinate, should fail validation`() = runTest {
        println("\n--- 🔴 TEST: `update with invalid coordinate` ---")

        val result = useCase("id-1", "Name", "Code", -100.0, 0.0)

        assertTrue(result.isFailure)
        assertEquals("Koordinat tidak valid.", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }
}