package com.pln.monitoringpln.domain.usecase.alat

import com.pln.monitoringpln.domain.model.Alat
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetAlatDetailUseCaseTest {
    private lateinit var fakeRepo: FakeAlatRepository
    private lateinit var useCase: GetAlatDetailUseCase

    @Before fun setUp() {
        fakeRepo = FakeAlatRepository()
        useCase = GetAlatDetailUseCase(fakeRepo)
    }

    @Test
    fun `get existing alat, return success`() = runTest {
        println("\n--- 🔴 TEST: `get existing alat` ---")

        // Arrange
        fakeRepo.addDummy(Alat("id-1", "K", "N", 0.0, 0.0, "Rusak"))

        println("  [Act] Cari ID 'id-1'...")
        val result = useCase("id-1")

        println("  [Assert] Cek data kembali...")
        assertTrue(result.isSuccess)
        assertEquals("Rusak", result.getOrNull()?.kondisi)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `get non-existent alat, return failure`() = runTest {
        println("\n--- 🔴 TEST: `get non-existent alat` ---")

        println("  [Act] Cari ID 'id-hantu'...")
        val result = useCase("id-hantu")

        assertTrue(result.isFailure)
        assertEquals("Alat tidak ditemukan", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `get with empty ID, return validation failure`() = runTest {
        println("\n--- 🔴 TEST: `get with empty ID` ---")

        println("  [Act] Cari ID kosong...")
        val result = useCase("   ")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("ID") == true)
        println("--- ✅ LULUS ---")
    }
}
