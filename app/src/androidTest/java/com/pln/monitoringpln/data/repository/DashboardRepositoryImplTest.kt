package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.DashboardRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DashboardRepositoryImplTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var dashboardRepository: DashboardRepository

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        // Initialize real Supabase Client for Integration Test
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
        }

        dashboardRepository = DashboardRepositoryImpl(supabaseClient)
    }

    @Test
    fun get_dashboard_summary_should_return_valid_data() = runBlocking {
        println(logHeader.format("Integration: Get Dashboard Summary"))
        
        // When
        val result = dashboardRepository.getDashboardSummary()

        // Then
        assertTrue(result.isSuccess)
        val summary = result.getOrNull()
        println(logAssert.format("Summary retrieved: $summary"))
        
        // Basic validation (assuming DB might be empty or not, but shouldn't crash)
        assertTrue(summary != null)
        assertTrue(summary!!.totalAlat >= 0)
        assertTrue(summary.totalTeknisi >= 0)
        
        println(logResult)
    }
}
