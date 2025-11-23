package com.pln.monitoringpln.domain.usecase.dashboard

import com.pln.monitoringpln.domain.model.DashboardSummary
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetDashboardSummaryUseCaseTest {

    private lateinit var fakeRepo: FakeDashboardRepository
    private lateinit var useCase: GetDashboardSummaryUseCase

    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        fakeRepo = FakeDashboardRepository()
        useCase = GetDashboardSummaryUseCase(fakeRepo)
    }

    // ==========================================
    // 1. HAPPY PATH & CALCULATION LOGIC
    // ==========================================

    @Test
    fun `get summary normal data, should return correct data and 50 percent rate`() = runTest {
        println(logHeader.format("Happy Path: 50% Completion"))

        val dummy = DashboardSummary(
            totalTugas = 10,
            tugasToDo = 2,
            tugasInProgress = 3,
            tugasDone = 5 // 50%
        )
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        val data = result.getOrNull()!!

        assertEquals(10, data.totalTugas)
        assertEquals(50.0, data.getCompletionRate(), 0.01) // Delta 0.01 untuk toleransi float
        println(logResult)
    }

    @Test
    fun `get summary all done, should return 100 percent rate`() = runTest {
        println(logHeader.format("Happy Path: 100% Completion"))

        val dummy = DashboardSummary(totalTugas = 50, tugasDone = 50)
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        assertEquals(100.0, result.getOrNull()!!.getCompletionRate(), 0.0)
        println(logResult)
    }

    @Test
    fun `get summary repeating decimal, should handle precision (1 of 3)`() = runTest {
        println(logHeader.format("Calculation: 33.333...% Precision"))

        val dummy = DashboardSummary(totalTugas = 3, tugasDone = 1)
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        // Harusnya 33.3333...
        val rate = result.getOrNull()!!.getCompletionRate()
        assertTrue(rate > 33.33 && rate < 33.34)
        println(logResult)
    }

    // ==========================================
    // 2. EDGE CASES (Zero & Boundaries)
    // ==========================================

    @Test
    fun `get summary with ZERO tasks, should return 0 percent (No Division by Zero)`() = runTest {
        println(logHeader.format("Edge Case: Zero Tasks (0/0)"))

        val dummy = DashboardSummary(totalTugas = 0, tugasDone = 0)
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        assertEquals(0.0, result.getOrNull()!!.getCompletionRate(), 0.0)
        println(logResult)
    }

    @Test
    fun `get summary with Large Numbers, should handle without overflow`() = runTest {
        println(logHeader.format("Edge Case: Large Integers"))

        val dummy = DashboardSummary(
            totalTugas = 1_000_000,
            tugasDone = 500_000
        )
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        assertEquals(50.0, result.getOrNull()!!.getCompletionRate(), 0.0)
        println(logResult)
    }

    // ==========================================
    // 3. DATA INTEGRITY (Weird Data from DB)
    // ==========================================

    @Test
    fun `repo returns Negative Values (Bug in DB), logic should still safe`() = runTest {
        println(logHeader.format("Data Integrity: Negative Values"))

        // Simulasi bug query DB yang return -5
        val dummy = DashboardSummary(totalTugas = 10, tugasDone = -5)
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        val data = result.getOrNull()!!

        // Rate = -5 / 10 * 100 = -50.0
        // Logic DTO 'bodoh' (hanya hitung), UI nanti yang harus handle tampilan negatif
        // Tapi minimal aplikasi TIDAK CRASH.
        assertEquals(-50.0, data.getCompletionRate(), 0.0)
        println(logResult)
    }

    @Test
    fun `repo returns Inconsistent Data (Total less than Done), should accept as is`() = runTest {
        println(logHeader.format("Data Integrity: Total < Done (Impossible Data)"))

        // Total 5, tapi yang selesai 10 (Aneh)
        val dummy = DashboardSummary(totalTugas = 5, tugasDone = 10)
        fakeRepo.summaryToReturn = dummy

        val result = useCase()
        val data = result.getOrNull()!!

        // Rate = 200%
        assertEquals(200.0, data.getCompletionRate(), 0.0)
        println(logResult)
    }

    // ==========================================
    // 4. FAILURE SCENARIOS
    // ==========================================

    @Test
    fun `repo failure (network error), should return failure result`() = runTest {
        println(logHeader.format("Negative Case: Repo Failure"))

        fakeRepo.shouldFail = true
        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("Gagal mengambil data dashboard", result.exceptionOrNull()?.message)
        println(logResult)
    }
}