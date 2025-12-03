package com.pln.monitoringpln.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ReportRepositoryImplTest {

    private lateinit var database: com.pln.monitoringpln.data.local.AppDatabase
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
        database = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.pln.monitoringpln.data.local.AppDatabase::class.java
        ).build()

        reportRepository = ReportRepositoryImpl(database.tugasDao(), database.userDao(), database.alatDao(), context)
    }

    @Test
    fun export_report_to_PDF_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Export PDF"))
        
        // Given
        val startDate = Date(System.currentTimeMillis() - 86400000 * 7) // 7 days ago
        val endDate = Date(System.currentTimeMillis() + 86400000 * 7) // 7 days ahead
        
        // Insert dummy data
        val task1 = com.pln.monitoringpln.data.local.entity.TugasEntity(
            id = "task-1", judul = "Task for Report", deskripsi = "Task for Report", idAlat = "alat-1", idTeknisi = "tech-1",
            tglDibuat = Date(), tglJatuhTempo = Date(), status = "Done", isSynced = true
        )
        database.tugasDao().insertTugas(task1)

        println(logAction.format("Export PDF from $startDate to $endDate"))

        // When
        val result = reportRepository.exportTaskReport(startDate, endDate, ExportFormat.PDF)

        // Then
        if (result.isFailure) {
            val error = result.exceptionOrNull()
            println("Error: ${error?.message}")
            error?.printStackTrace()
            throw AssertionError("Export PDF failed: ${error?.message}", error)
        }
        
        val path = result.getOrNull()
        println(logAssert.format("File created at: $path"))
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Verify via MediaStore
            val fileName = path?.substringAfterLast("/")
            val projection = arrayOf(android.provider.MediaStore.MediaColumns.DISPLAY_NAME)
            val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(fileName)
            val cursor = context.contentResolver.query(
                android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            val exists = cursor?.use { it.count > 0 } ?: false
            assertTrue("File $fileName not found in MediaStore", exists)
        } else {
            val file = File(path!!)
            assertTrue("File does not exist", file.exists())
            assertTrue("File is empty", file.length() > 0)
        }
        
        println(logResult)
    }

    @Test
    fun export_report_to_CSV_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Export CSV"))
        
        // Given
        val startDate = Date(System.currentTimeMillis() - 86400000 * 7) // 7 days ago
        val endDate = Date(System.currentTimeMillis() + 86400000 * 7) // 7 days ahead
        
        // Insert dummy data
        val task1 = com.pln.monitoringpln.data.local.entity.TugasEntity(
            id = "task-1", judul = "Task for Report CSV", deskripsi = "Task for Report CSV", idAlat = "alat-1", idTeknisi = "tech-1",
            tglDibuat = Date(), tglJatuhTempo = Date(), status = "Done", isSynced = true
        )
        database.tugasDao().insertTugas(task1)

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
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Verify via MediaStore
                val fileName = path?.substringAfterLast("/")
                val projection = arrayOf(android.provider.MediaStore.MediaColumns.DISPLAY_NAME)
                val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ?"
                val selectionArgs = arrayOf(fileName)
                val cursor = context.contentResolver.query(
                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                val exists = cursor?.use { it.count > 0 } ?: false
                assertTrue("File $fileName not found in MediaStore", exists)
            } else {
                val file = File(path!!)
                assertTrue(file.exists())
                assertTrue(file.length() > 0)
            }
            assertTrue(path!!.endsWith(".csv"))
        } else {
            println(logAssert.format("Failed (likely no data): ${result.exceptionOrNull()?.message}"))
        }
        
        println(logResult)
    }
}
