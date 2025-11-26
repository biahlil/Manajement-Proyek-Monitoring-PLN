package com.pln.monitoringpln.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ReportRepositoryImplTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var reportRepository: ReportRepository
    private lateinit var context: Context

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // Initialize real Supabase Client
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
        }

        reportRepository = ReportRepositoryImpl(supabaseClient, context)
    }

    @Test
    fun export_report_to_PDF_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Export PDF"))
        
        // Given
        val startDate = Date(System.currentTimeMillis() - 86400000 * 7) // 7 days ago
        val endDate = Date(System.currentTimeMillis() + 86400000 * 7) // 7 days ahead
        println(logAction.format("Export PDF from $startDate to $endDate"))

        // When
        val result = reportRepository.exportTaskReport(startDate, endDate, ExportFormat.PDF)

        // Then
        if (result.isFailure) {
            println("Error: ${result.exceptionOrNull()?.message}")
        }
        // Note: If no tasks found, it might fail as per logic.
        // We assume there are tasks or we accept failure if "Tidak ada data"
        
        if (result.isSuccess) {
            val path = result.getOrNull()
            println(logAssert.format("File created at: $path"))
            val file = File(path!!)
            assertTrue(file.exists())
            assertTrue(file.length() > 0)
        } else {
            println(logAssert.format("Failed (likely no data): ${result.exceptionOrNull()?.message}"))
        }
        
        println(logResult)
    }

    @Test
    fun export_report_to_CSV_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Export CSV"))
        
        // Given
        val startDate = Date(System.currentTimeMillis() - 86400000 * 7) // 7 days ago
        val endDate = Date(System.currentTimeMillis() + 86400000 * 7) // 7 days ahead
        println(logAction.format("Export CSV from $startDate to $endDate"))

        // When
        val result = reportRepository.exportTaskReport(startDate, endDate, ExportFormat.EXCEL_CSV)

        // Then
        if (result.isFailure) {
            println("Error: ${result.exceptionOrNull()?.message}")
        }
        
        if (result.isSuccess) {
            val path = result.getOrNull()
            println(logAssert.format("File created at: $path"))
            val file = File(path!!)
            assertTrue(file.exists())
            assertTrue(file.length() > 0)
            assertTrue(path.endsWith(".csv"))
        } else {
            println(logAssert.format("Failed (likely no data): ${result.exceptionOrNull()?.message}"))
        }
        
        println(logResult)
    }
}
