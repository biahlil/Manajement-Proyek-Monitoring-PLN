package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.utils.TestObjects
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
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
    private var teknisiId = "b71712fc-b77d-483c-9bad-aedb00da764d" // Real Teknisi ID

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    private lateinit var database: com.pln.monitoringpln.data.local.AppDatabase

    @Before
    fun setUp() = runBlocking {
        // Initialize real Supabase Client
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY,
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth)
        }

        // Login as Admin
        try {
            supabaseClient.auth.signInWith(Email) {
                email = "boss@pln.co.id"
                password = "password123"
            }
        } catch (e: Exception) {
            println("Login failed (might be already logged in or network issue): ${e.message}")
        }

        // Initialize In-Memory Room Database
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        database = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.pln.monitoringpln.data.local.AppDatabase::class.java,
        ).allowMainThreadQueries().build()

        // Use real user ID if available, otherwise mock
        currentUserId = supabaseClient.auth.currentUserOrNull()?.id ?: "test-user-id"
        teknisiId = currentUserId
        // Update teknisiId to be current user (assuming they are teknisi or just for FK validity)
        // If the table has FK to users, this must be valid.
        // If teknisiId is a var in the class, I need to update it.
        // But teknisiId is val. I should make it var or update it here.
        // I'll change the val definition to var or lazy.
        // But I can't change class property easily with replace_file_content if it's far away.
        // I'll just change the property definition.
        // Initialize DataSources for AlatRepo
        val alatLocalDataSource = com.pln.monitoringpln.data.local.datasource.AlatLocalDataSource(database.alatDao())
        val alatRemoteDataSource = com.pln.monitoringpln.data.remote.AlatRemoteDataSource(supabaseClient)
        alatRepository = AlatRepositoryImpl(alatLocalDataSource, alatRemoteDataSource)

        // Initialize DataSources for TugasRepo
        val tugasLocalDataSource = com.pln.monitoringpln.data.local.datasource.TugasLocalDataSource(database.tugasDao())
        val tugasRemoteDataSource = com.pln.monitoringpln.data.remote.TugasRemoteDataSource(supabaseClient)
        tugasRepository = TugasRepositoryImpl(tugasLocalDataSource, tugasRemoteDataSource)
    }

    private val createdTugasIds = mutableListOf<String>()
    private val createdAlatIds = mutableListOf<String>()

    @org.junit.After
    fun tearDown() {
        database.close()

        // Cleanup Remote Data
        runBlocking {
            // Delete Tugas first (FK constraint)
            if (createdTugasIds.isNotEmpty()) {
                try {
                    createdTugasIds.forEach { id ->
                        supabaseClient.postgrest["tugas"].delete {
                            filter { eq("id", id) }
                        }
                        println("Cleaned up tugas: $id")
                    }
                } catch (e: Exception) {
                    println("Failed to cleanup tugas: ${e.message}")
                }
            }

            // Delete Alat
            if (createdAlatIds.isNotEmpty()) {
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

    private suspend fun createTestAlat(): String {
        val uniqueId = java.util.UUID.randomUUID().toString()
        val uniqueCode = "TEST-${java.util.UUID.randomUUID()}"
        val alat = TestObjects.ALAT_VALID.copy(
            id = uniqueId,
            kodeAlat = uniqueCode,
            namaAlat = "Test Alat ${System.currentTimeMillis()}",
        )
        val result = alatRepository.insertAlat(alat)
        if (result.isFailure) {
            throw IllegalStateException("Failed to create test alat: ${result.exceptionOrNull()?.message}")
        }
        createdAlatIds.add(uniqueId)

        // Check if Alat is synced
        val localAlat = database.alatDao().getAlatDetail(uniqueId)
        println("Local Alat isSynced: ${localAlat?.isSynced}")
        if (localAlat?.isSynced == false) {
            println("WARNING: Alat was not synced to remote. Tugas insert might fail due to FK constraint.")
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
            idAlat = alatId,
        )
        println(logAction.format("Create task: ${tugas.deskripsi} with ID: $uniqueId"))

        // When
        // When
        val createResult = tugasRepository.createTask(tugas)
        val createdTugas = createResult.getOrNull()
        createdTugas?.id?.let { createdTugasIds.add(it) }
        println(logAssert.format(createdTugas?.deskripsi))

        // Then
        assertTrue(createResult.isSuccess)
        assertNotNull(createdTugas)
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
            idAlat = alatId,
        )
        val createResult = tugasRepository.createTask(tugas)
        if (createResult.isFailure) {
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
    fun update_task_status_to_todo_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Revert Task Status to TODO"))

        // Given: Create Alat and Task (IN_PROGRESS)
        val alatId = createTestAlat()
        val uniqueDesc = "REVERT-STATUS-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val tugas = TestObjects.TUGAS_IN_PROGRESS.copy(
            id = uniqueId,
            deskripsi = uniqueDesc,
            idTeknisi = teknisiId,
            idAlat = alatId,
        )
        tugasRepository.createTask(tugas)

        // When
        val result = tugasRepository.updateTaskStatus(uniqueId, "TODO")

        // Then
        assertTrue(result.isSuccess)
        val updatedTask = tugasRepository.getTaskDetail(uniqueId).getOrNull()
        assertTrue(updatedTask?.status == "TODO")
        println(logAssert.format("Task status reverted to TODO successfully"))

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
            idAlat = alatId,
        )
        val createResult = tugasRepository.createTask(tugas)
        if (createResult.isFailure) {
            throw IllegalStateException("Failed to create task")
        }

        // Check if synced
        val localTask = database.tugasDao().getTugasById(uniqueId)
        println("Local task isSynced: ${localTask?.isSynced}")
        if (localTask?.isSynced == false) {
            println("WARNING: Task was not synced to remote. getTasksByAlat might fail if it relies on remote.")
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
