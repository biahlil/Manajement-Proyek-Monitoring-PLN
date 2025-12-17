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
        val result = useCase(namaAlat = "Trafo A", kodeAlat = "TRF-1", latitude = -6.2, longitude = 106.8)

        println("  [Assert] Cek sukses dan kondisi default...")
        assertTrue(result.isSuccess)
        assertEquals("Normal", fakeRepo.lastSavedAlat?.status)
        assertEquals("", fakeRepo.lastSavedAlat?.kondisi)

        println("--- ✅ LULUS ---")
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `add valid alat with location name, should save correctly`() = runTest {
        println("\n--- 🔴 TEST: `add valid alat with location name` ---")

        println("  [Act] Memanggil UseCase dengan location name...")
        val result = useCase(namaAlat = "Trafo B", kodeAlat = "TRF-2", latitude = -3.3, longitude = 114.5, locationName = "Banjarmasin")

        println("  [Assert] Cek sukses dan location name...")
        assertTrue(result.isSuccess)
        assertEquals("Banjarmasin", fakeRepo.lastSavedAlat?.locationName)

        println("--- ✅ LULUS ---")
    }

    // --- Edge Case: Whitespace ---
    @Test
    fun `add alat with whitespace name, should fail`() = runTest {
        println("\n--- 🔴 TEST: `add alat with whitespace name` ---")

        println("  [Act] Input nama spasi kosong...")
        val result = useCase(namaAlat = "   ", kodeAlat = "TRF-1", latitude = 0.0, longitude = 0.0)

        println("  [Assert] Cek failure message...")
        assertTrue(result.isFailure)
        assertEquals("Nama alat tidak boleh kosong.", result.exceptionOrNull()?.message)

        println("--- ✅ LULUS ---")
    }

    @Test
    fun `add alat with whitespace code, should fail`() = runTest {
        println("\n--- 🔴 TEST: `add alat with whitespace code` ---")
        val result = useCase(namaAlat = "Trafo A", kodeAlat = "   ", latitude = 0.0, longitude = 0.0)

        assertTrue(result.isFailure)
        assertEquals("Kode alat tidak boleh kosong.", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }

    // --- Boundary Values: Coordinates ---
    @Test
    fun `add alat with EXACT boundary latitude, should pass`() = runTest {
        println("\n--- 🔴 TEST: `add alat boundary latitude (90.0)` ---")

        println("  [Act] Input Lat 90.0 (Valid)...")
        val result = useCase(namaAlat = "Kutub Utara", kodeAlat = "N-01", latitude = 90.0, longitude = 0.0)

        assertTrue("Latitude 90.0 harusnya valid", result.isSuccess)
        println("--- ✅ LULUS ---")
    }

    @Test
    fun `add alat with SLIGHTLY invalid latitude, should fail`() = runTest {
        println("\n--- 🔴 TEST: `add alat invalid latitude (90.000001)` ---")

        println("  [Act] Input Lat 90.000001 (Invalid)...")
        val result = useCase(namaAlat = "Invalid Lat", kodeAlat = "X-01", latitude = 90.000001, longitude = 0.0)

        assertTrue(result.isFailure)
        assertEquals("Koordinat tidak valid.", result.exceptionOrNull()?.message)
        println("--- ✅ LULUS ---")
    }
}
