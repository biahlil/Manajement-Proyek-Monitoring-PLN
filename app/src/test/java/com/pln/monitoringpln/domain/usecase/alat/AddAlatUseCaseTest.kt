package com.pln.monitoringpln.domain.usecase.alat

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddAlatUseCaseTest {
    private lateinit var fakeRepo: FakeAlatRepository
    private lateinit var useCase: AddAlatUseCase

    @Before fun setUp() {
        fakeRepo = FakeAlatRepository()
        useCase = AddAlatUseCase(fakeRepo)
    }

    @Test
    fun `add valid alat, should save with default condition`() = runTest {
        println("\n--- 🔴 TEST: `add valid alat` ---")

        println("  [Act] Memanggil UseCase dengan input valid...")
        val result = useCase("Trafo A", "TRF-1", -6.2, 106.8)

        println("  [Assert] Cek sukses dan kondisi default...")
        assertTrue(result.isSuccess)
        assertEquals("Normal", fakeRepo.lastSavedAlat?.kondisi)

        println("--- ✅ LULUS ---")
    }

    // --- Edge Case: Whitespace ---
    @Test
    fun `add alat with whitespace name, should fail`() = runTest {
        println("\n--- 🔴 TEST: `add alat with whitespace name` ---")

        println("  [Act] Input nama spasi kosong...")
        val result = useCase("   ", "TRF-1", 0.0, 0.0)

        println("  [Assert] Cek failure message...")
        assertTrue(result.isFailure)
        assertEquals("Nama alat tidak boleh kosong.", result.exceptionOrNull()?.message)

        println("--- ✅ LULUS ---")
    }

    @Test
    fun `add alat with whitespace code, should fail`() = runTest {
        println("\n--- 🔴 TEST: `add alat with whitespace code` ---")
        val result = useCase("Trafo A", "   ", 0.0, 0.0)

        assertTrue(result.isFailure)
        assertEquals("Kode alat tidak boleh kosong.", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }

    // --- Boundary Values: Coordinates ---
    @Test
    fun `add alat with EXACT boundary latitude, should pass`() = runTest {
        println("\n--- 🔴 TEST: `add alat boundary latitude (90.0)` ---")

        println("  [Act] Input Lat 90.0 (Valid)...")
        val result = useCase("Kutub Utara", "N-01", 90.0, 0.0)

        assertTrue("Latitude 90.0 harusnya valid", result.isSuccess)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `add alat with SLIGHTLY invalid latitude, should fail`() = runTest {
        println("\n--- 🔴 TEST: `add alat invalid latitude (90.000001)` ---")

        println("  [Act] Input Lat 90.000001 (Invalid)...")
        val result = useCase("Invalid Lat", "X-01", 90.000001, 0.0)

        assertTrue(result.isFailure)
        assertEquals("Koordinat tidak valid.", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }
}
