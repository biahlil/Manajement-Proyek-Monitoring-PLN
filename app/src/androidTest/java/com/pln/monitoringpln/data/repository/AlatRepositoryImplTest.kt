package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.utils.TestObjects
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
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

    @Before
    fun setUp() = runBlocking {
        // Initialize real Supabase Client for Integration Test
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY
        ) {
            install(io.github.jan.supabase.gotrue.Auth)
            install(Postgrest)
        }

        // Login first to bypass RLS
        val authRepo = AuthRepositoryImpl(supabaseClient)
        try {
            val result = authRepo.login("boss@pln.co.id", "password123")
            println("Setup Login Result: ${result.isSuccess}")
            if (result.isFailure) {
                println("Setup Login Error: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            println("Setup Login Exception: ${e.message}")
        }

        alatRepository = AlatRepositoryImpl(supabaseClient)
    }

    @Test
    fun insert_and_get_alat_detail_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Insert & Get Alat"))
        
        // Given
        val uniqueCode = "TEST-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val alat = TestObjects.ALAT_VALID.copy(id = uniqueId, kodeAlat = uniqueCode)
        println(logAction.format("Insert alat: ${alat.kodeAlat} with ID: $uniqueId"))

        // When
        val insertResult = alatRepository.insertAlat(alat)

        // Then
        if (insertResult.isFailure) {
            println("Insert Error: ${insertResult.exceptionOrNull()?.message}")
        }
        assertTrue(insertResult.isSuccess)
        println(logAssert.format("Insert successful"))
        
        println(logResult)
    }

    @Test
    fun archive_alat_should_update_status() = runBlocking {
        println(logHeader.format("Integration: Archive Alat"))
        
        // Given: Insert first to get ID
        val uniqueCode = "ARCHIVE-TEST-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val alat = TestObjects.ALAT_VALID.copy(id = uniqueId, kodeAlat = uniqueCode)
        alatRepository.insertAlat(alat)
        
        // Fetch ID using the new repository method
        val fetchedAlatResult = alatRepository.getAlatByKode(uniqueCode)
        assertTrue(fetchedAlatResult.isSuccess)
        val id = fetchedAlatResult.getOrNull()?.id ?: throw IllegalStateException("Alat not found")
            
        println(logAction.format("Archive alat: $id"))

        // When
        val result = alatRepository.archiveAlat(id)

        // Then
        if (result.isFailure) {
            println("Archive Error: ${result.exceptionOrNull()?.message}")
        }
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
        val uniqueId = java.util.UUID.randomUUID().toString()
        val alat = TestObjects.ALAT_VALID.copy(id = uniqueId, kodeAlat = uniqueCode)
        alatRepository.insertAlat(alat)
        
        // Get ID
        val id = alatRepository.getAlatByKode(uniqueCode).getOrNull()?.id ?: throw IllegalStateException("Alat not found")

        // When
        val updatedAlat = alat.copy(id = id, namaAlat = "Updated Name", latitude = 1.0, longitude = 1.0)
        val result = alatRepository.updateAlatInfo(
            id = updatedAlat.id,
            nama = updatedAlat.namaAlat,
            kode = updatedAlat.kodeAlat,
            lat = updatedAlat.latitude,
            lng = updatedAlat.longitude
        )

        // Then
        assertTrue(result.isSuccess)
        val fetchedAlat = alatRepository.getAlatDetail(id).getOrNull()
        assertEquals("Updated Name", fetchedAlat?.namaAlat)
        assertEquals(1.0, fetchedAlat?.latitude)
        assertEquals(1.0, fetchedAlat?.longitude)
        println(logAssert.format("Alat info updated successfully"))
        
        println(logResult)
    }

    @Test
    fun update_alat_condition_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Update Alat Condition"))
        
        // Given: Insert first
        val uniqueCode = "UPDATE-COND-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val alat = TestObjects.ALAT_VALID.copy(id = uniqueId, kodeAlat = uniqueCode, kondisi = "Baik")
        alatRepository.insertAlat(alat)
        
        // Get ID
        val id = alatRepository.getAlatByKode(uniqueCode).getOrNull()?.id ?: throw IllegalStateException("Alat not found")

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
