package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.utils.TestObjects
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AlatRepositoryImplTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var alatRepository: AlatRepository

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    private lateinit var database: com.pln.monitoringpln.data.local.AppDatabase

    @Before
    fun setUp() {
        // Initialize real Supabase Client for Integration Test
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
        }

        // Initialize In-Memory Room Database
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        database = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.pln.monitoringpln.data.local.AppDatabase::class.java
        ).allowMainThreadQueries().build()

        // Initialize DataSources
        val localDataSource = com.pln.monitoringpln.data.local.datasource.AlatLocalDataSource(database.alatDao())
        val remoteDataSource = com.pln.monitoringpln.data.remote.AlatRemoteDataSource(supabaseClient)

        alatRepository = AlatRepositoryImpl(localDataSource, remoteDataSource)
    }

    private val createdAlatIds = mutableListOf<String>()

    @org.junit.After
    fun tearDown() {
        database.close()
        
        // Cleanup Remote Data
        if (createdAlatIds.isNotEmpty()) {
            runBlocking {
                try {
                    createdAlatIds.forEach { id ->
                        supabaseClient.postgrest["alat"].delete {
                            filter { eq("id", id) }
                        }
                        println("Cleaned up alat: $id")
                    }
                } catch (e: Exception) {
                    println("Failed to cleanup alat: ${e.message}")
                }
            }
        }
    }

    @Test
    fun insert_and_get_alat_detail_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Insert & Get Alat"))
        
        // Given
        val uniqueCode = "TEST-${System.currentTimeMillis()}"
        val alat = TestObjects.ALAT_VALID.copy(kodeAlat = uniqueCode)
        println(logAction.format("Insert alat: ${alat.kodeAlat}"))

        // When
        val insertResult = alatRepository.insertAlat(alat)

        // Then
        assertTrue(insertResult.isSuccess)
        
        // Track ID for cleanup
        val fetched = alatRepository.getAlatByKode(uniqueCode).getOrNull()
        fetched?.id?.let { createdAlatIds.add(it) }
        
        println(logAssert.format("Insert successful"))
        
        println(logResult)
    }

    @Test
    fun archive_alat_should_update_status() = runBlocking {
        println(logHeader.format("Integration: Archive Alat"))
        
        // Given: Insert first to get ID
        val uniqueCode = "ARCHIVE-TEST-${System.currentTimeMillis()}"
        val alat = TestObjects.ALAT_VALID.copy(kodeAlat = uniqueCode)
        alatRepository.insertAlat(alat)
        
        // Fetch ID using the new repository method
        val fetchedAlatResult = alatRepository.getAlatByKode(uniqueCode)
        assertTrue(fetchedAlatResult.isSuccess)
        val id = fetchedAlatResult.getOrNull()?.id ?: throw IllegalStateException("Alat not found")
        createdAlatIds.add(id)
            
        println(logAction.format("Archive alat: $id"))

        // When
        val result = alatRepository.archiveAlat(id)

        // Then
        assertTrue(result.isSuccess)
        
        // Verify
        val archivedAlat = alatRepository.getAlatDetail(id).getOrNull()
        assertEquals("ARCHIVED", archivedAlat?.status)
        assertTrue(archivedAlat?.isArchived == true)
        println(logAssert.format("Alat archived successfully"))
        
        println(logResult)
    }
    @Test
    fun update_alat_info_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Update Alat Info"))
        
        // Given: Insert first
        val uniqueCode = "UPDATE-INFO-${System.currentTimeMillis()}"
        val alat = TestObjects.ALAT_VALID.copy(kodeAlat = uniqueCode)
        alatRepository.insertAlat(alat)
        
        // Get ID
        val id = alatRepository.getAlatByKode(uniqueCode).getOrNull()?.id ?: throw IllegalStateException("Alat not found")
        createdAlatIds.add(id)

        // When
        val updatedAlat = alat.copy(id = id, namaAlat = "Updated Name", latitude = 1.0, longitude = 1.0, locationName = "New Location")
        val result = alatRepository.updateAlatInfo(
            id = updatedAlat.id,
            nama = updatedAlat.namaAlat,
            kode = updatedAlat.kodeAlat,
            lat = updatedAlat.latitude,
            lng = updatedAlat.longitude,
            locationName = updatedAlat.locationName
        )

        // Then
        assertTrue(result.isSuccess)
        val fetchedAlat = alatRepository.getAlatDetail(id).getOrNull()
        assertEquals("Updated Name", fetchedAlat?.namaAlat)
        assertEquals(1.0, fetchedAlat?.latitude)
        assertEquals(1.0, fetchedAlat?.longitude)
        assertEquals("New Location", fetchedAlat?.locationName)
        println(logAssert.format("Alat info updated successfully"))
        
        println(logResult)
    }

    @Test
    fun update_alat_condition_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Update Alat Condition"))
        
        // Given: Insert first
        val uniqueCode = "UPDATE-COND-${System.currentTimeMillis()}"
        val alat = TestObjects.ALAT_VALID.copy(kodeAlat = uniqueCode, kondisi = "Baik")
        alatRepository.insertAlat(alat)
        
        // Get ID
        val id = alatRepository.getAlatByKode(uniqueCode).getOrNull()?.id ?: throw IllegalStateException("Alat not found")
        createdAlatIds.add(id)

        // When
        val result = alatRepository.updateAlatCondition(id, "Rusak")

        // Then
        assertTrue(result.isSuccess)
        val fetchedAlat = alatRepository.getAlatDetail(id).getOrNull()
        assertEquals("Rusak", fetchedAlat?.kondisi)
        println(logAssert.format("Alat condition updated successfully"))
        
        println(logResult)
    }
}
