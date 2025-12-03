package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.DashboardRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

import kotlinx.coroutines.flow.first

class DashboardRepositoryImplTest {

    private lateinit var database: com.pln.monitoringpln.data.local.AppDatabase
    private lateinit var dashboardRepository: DashboardRepository

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        database = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.pln.monitoringpln.data.local.AppDatabase::class.java
        ).build()

        dashboardRepository = DashboardRepositoryImpl(database.alatDao(), database.tugasDao())
    }

    @org.junit.After
    fun tearDown() {
        database.close()
    }

    @Test
    fun get_dashboard_summary_should_return_valid_data() = runBlocking {
        println(logHeader.format("Integration: Get Dashboard Summary (Local)"))
        
        // Given: Insert Dummy Data
        val alat1 = com.pln.monitoringpln.data.local.entity.AlatEntity(
            id = "alat-1", namaAlat = "Trafo A", kodeAlat = "TR-A", 
            latitude = 0.0, longitude = 0.0, status = "Active", kondisi = "Baik", 
            lastModifiedById = "user-1", isArchived = false, isSynced = true
        )
        database.alatDao().insertAlat(alat1)

        val task1 = com.pln.monitoringpln.data.local.entity.TugasEntity(
            id = "task-1", judul = "Fix Trafo", deskripsi = "Fix Trafo", idAlat = "alat-1", idTeknisi = "tech-1",
            tglDibuat = java.util.Date(), tglJatuhTempo = java.util.Date(), status = "To Do", isSynced = true
        )
        val task2 = com.pln.monitoringpln.data.local.entity.TugasEntity(
            id = "task-2", judul = "Check Trafo", deskripsi = "Check Trafo", idAlat = "alat-1", idTeknisi = "tech-1",
            tglDibuat = java.util.Date(), tglJatuhTempo = java.util.Date(), status = "Done", isSynced = true
        )
        database.tugasDao().insertAll(listOf(task1, task2))

        // When
        val summary = dashboardRepository.getDashboardSummary().first()

        // Then
        println(logAssert.format("Summary retrieved: $summary"))
        
        assertTrue(summary != null)
        assertTrue("Total Alat should be 1", summary.totalAlat == 1)
        assertTrue("Total Tugas should be 2", summary.totalTugas == 2)
        assertTrue("To Do should be 1", summary.tugasToDo == 1)
        assertTrue("Done should be 1", summary.tugasDone == 1)
        
        println(logResult)
    }
}
