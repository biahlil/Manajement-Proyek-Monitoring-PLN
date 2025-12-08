package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.utils.TestObjects
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AlatSyncTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var alatRepository: AlatRepository
    private lateinit var database: com.pln.monitoringpln.data.local.AppDatabase

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() = runBlocking {
        // Initialize real Supabase Client
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY,
        ) {
            install(Postgrest)
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

        // Initialize DataSources
        val localDataSource = com.pln.monitoringpln.data.local.datasource.AlatLocalDataSource(database.alatDao())
        val remoteDataSource = com.pln.monitoringpln.data.remote.AlatRemoteDataSource(supabaseClient)

        alatRepository = AlatRepositoryImpl(localDataSource, remoteDataSource)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun sync_should_push_local_changes_to_remote() = runBlocking {
        println(logHeader.format("Integration: Sync Push (Local -> Remote)"))

        // Given: Insert Local (Unsynced)
        val uniqueCode = "SYNC-PUSH-${System.currentTimeMillis()}"
        val uniqueId = java.util.UUID.randomUUID().toString()
        val alat = TestObjects.ALAT_VALID.copy(id = uniqueId, kodeAlat = uniqueCode)
        alatRepository.insertAlat(alat) // This inserts to local with isSynced=false

        // Verify it is unsynced locally
        val localBefore = database.alatDao().getAlatByKode(uniqueCode)
        assertNotNull(localBefore)
        assertTrue(localBefore?.isSynced == false)
        println(logAction.format("Inserted local unsynced alat: $uniqueCode"))

        // When: Sync
        val result = alatRepository.sync()

        // Then
        if (result.isFailure) {
            println("Sync failed with error: ${result.exceptionOrNull()}")
            result.exceptionOrNull()?.printStackTrace()
        }
        assertTrue("Sync should be successful", result.isSuccess)
        println(logAssert.format("Sync successful"))

        // Verify Local is now Synced
        val localAfter = database.alatDao().getAlatByKode(uniqueCode)
        assertTrue(localAfter?.isSynced == true)
        println(logAssert.format("Local alat marked as synced"))

        // Verify Remote has the data
        val remoteResult = supabaseClient.postgrest["alat"]
            .select {
                filter { eq("kode_alat", uniqueCode) }
            }.decodeSingleOrNull<com.pln.monitoringpln.data.model.AlatDto>()

        assertNotNull(remoteResult)
        println(logAssert.format("Remote data found: ${remoteResult?.kodeAlat}"))

        // Cleanup: Delete from Remote
        try {
            supabaseClient.postgrest["alat"].delete {
                filter {
                    eq("id", uniqueId)
                }
            }
            println(logAction.format("Cleaned up remote test data: $uniqueId"))
        } catch (e: Exception) {
            println("Failed to cleanup remote data: ${e.message}")
        }

        println(logResult)
    }
}
