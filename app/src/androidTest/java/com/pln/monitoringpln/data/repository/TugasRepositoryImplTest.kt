package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.utils.TestObjects
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TugasRepositoryImplTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var tugasRepository: TugasRepository
    private lateinit var alatRepository: AlatRepository
    private lateinit var currentUserId: String // Admin ID
    private val teknisiId = "b71712fc-b77d-483c-9bad-aedb00da764d" // Real Teknisi ID

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
            install(Auth)
            install(Postgrest)
            install(Storage)
        }

        // Login as Admin to have full access (Create, etc)
        val authRepo = AuthRepositoryImpl(supabaseClient)
        authRepo.login("boss@pln.co.id", "password123")
        
        // Get current user ID (Admin)
        val user = supabaseClient.auth.currentUserOrNull()
        currentUserId = user?.id ?: throw IllegalStateException("User not logged in")

        tugasRepository = TugasRepositoryImpl(supabaseClient)
        alatRepository = AlatRepositoryImpl(supabaseClient)
    }

    private suspend fun createTestAlat(): String {
        val uniqueId = java.util.UUID.randomUUID().toString()
        val uniqueCode = "TEST-${java.util.UUID.randomUUID()}"
        val alat = TestObjects.ALAT_VALID.copy(
            id = uniqueId,
            kodeAlat = uniqueCode,
            namaAlat = "Test Alat ${System.currentTimeMillis()}"
        )
        val result = alatRepository.insertAlat(alat)
        if (result.isFailure) {
            throw IllegalStateException("Failed to create test alat: ${result.exceptionOrNull()?.message}")
        }
        return uniqueId
    }

    @Test
    fun create_and_get_task_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Create & Get Task"))
        
        // Given: Create Alat first
        val alatId = createTestAlat()
        
        val uniqueId = java.util.UUID.randomUUID().toString()
        val tugas = TestObjects.TUGAS_TODO.copy(
            id = uniqueId, 
            deskripsi = "Test Task ${System.currentTimeMillis()}",
            idTeknisi = teknisiId,
            idAlat = alatId
        )
        println(logAction.format("Create task: ${tugas.deskripsi} with ID: $uniqueId"))

        // When
        val createResult = tugasRepository.createTask(tugas)
        println(logAssert.format(createResult?.deskripsi))

        // Then
        assertNotNull(createResult)
        println(logAssert.format("Create successful"))

        // Get Tasks (Query by Teknisi ID)
        val getResult = tugasRepository.getTasksByTeknisi(teknisiId)
        assertTrue(getResult.isSuccess)
        val tasks = getResult.getOrNull()
        
        assertTrue(tasks?.any { it.deskripsi == tugas.deskripsi } == true)
        println(logAssert.format("Task found in list"))
        
        println(logResult)
    }

    @Test
    fun update_task_status_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Update Task Status"))
        
        // Given: Create Alat and Task
        val alatId = createTestAlat()
        val uniqueDesc = "UPDATE-STATUS-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val tugas = TestObjects.TUGAS_TODO.copy(
            id = uniqueId, 
            deskripsi = uniqueDesc,
            idTeknisi = teknisiId,
            idAlat = alatId
        )
        val createResult = tugasRepository.createTask(tugas)
        if (createResult == null) {
             throw IllegalStateException("Failed to create task")
        }
        
        // Get ID via list
        val tasks = tugasRepository.getTasksByTeknisi(teknisiId).getOrNull()
        val createdTask = tasks?.find { it.deskripsi == uniqueDesc }
        val id = createdTask?.id ?: throw IllegalStateException("Task not found")

        // When
        val result = tugasRepository.updateTaskStatus(id, "IN_PROGRESS")

        // Then
        assertTrue(result.isSuccess)
        val updatedTask = tugasRepository.getTaskDetail(id).getOrNull()
        assertTrue(updatedTask?.status == "IN_PROGRESS")
        println(logAssert.format("Task status updated successfully"))
        
        println(logResult)
    }

    @Test
    fun get_tasks_by_alat_should_return_list() = runBlocking {
        println(logHeader.format("Integration: Get Tasks by Alat"))
        
        // Given: Create Alat and Task
        val alatId = createTestAlat()
        val uniqueDesc = "ALAT-TASK-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val tugas = TestObjects.TUGAS_TODO.copy(
            id = uniqueId, 
            deskripsi = uniqueDesc,
            idTeknisi = teknisiId,
            idAlat = alatId
        )
        val createResult = tugasRepository.createTask(tugas)
        if (createResult == null) {
             throw IllegalStateException("Failed to create task")
        }

        // When
        val result = tugasRepository.getTasksByAlat(alatId)

        // Then
        assertTrue(result.isSuccess)
        val tasks = result.getOrNull()
        assertTrue(tasks?.any { it.deskripsi == uniqueDesc } == true)
        println(logAssert.format("Tasks by alat retrieved successfully"))
        
        println(logResult)
    }
}
